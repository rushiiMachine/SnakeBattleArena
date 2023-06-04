package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.*;
import apcs.snakebattlearena.models.DeathReason;
import apcs.snakebattlearena.models.Direction;
import apcs.snakebattlearena.models.MoveData;
import apcs.snakebattlearena.models.TickData;
import apcs.snakebattlearena.server.websocket.WebsocketHandler;
import apcs.snakebattlearena.utils.Predicates;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles all the game logic at a fixed tick rate.
 */
@Service
@EnableScheduling
@SuppressWarnings("unused")
public class GameService {
    /**
     * Queued moves between each tick to be processed at the next tick.
     * <br/>
     * Player name -> Latest received move data
     */
    private final HashMap<String, MoveData> queuedMoves = new HashMap<>();

    /**
     * All entities on this board (snake, apple, wall, etc...)
     */
    private final HashSet<Entity<?>> entities = new HashSet<>();

    /**
     * Dual-purpose lock for {@link GameService#queuedMoves} and {@link GameService#entities}
     * to prevent race conditions between message receivers and the game thread.
     */
    private final ReentrantReadWriteLock tickLock = new ReentrantReadWriteLock();

    /**
     * Board with all available squares. Squares are stored by <code>board[x][y]</code>.
     */
    private final BoardSquare[][] board;

    /**
     * Board width/height initialized when GameService is.
     */
    private final int boardWidth, boardHeight;

    /**
     * Logger for this service to log debug info.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The websocket message sender/receiver taken from Spring DI.
     */
    @Autowired
    private WebsocketHandler websocket;

    public GameService(GameConfig config) {
        this.boardWidth = config.getBoardWidth();
        this.boardHeight = config.getBoardHeight();

        // Initialize board
        this.board = new BoardSquare[boardWidth][boardHeight];
        Arrays.parallelSetAll(this.board, i -> {
            BoardSquare[] column = new BoardSquare[boardHeight];
            Arrays.parallelSetAll(column, i2 -> new BoardSquare());
            return column;
        });

        // TODO: remove this
        Snake s = new ServerSnake(
                "rusher",
                Color.PINK,
                new Point(0, 0)
        );
        entities.add(s);
        board[0][0].addOccupier(s);
        for (int i = 0; i < 50; i++) {
            addApple();
        }
        printBoard();
    }

    /**
     * Queues up a player move to be processed at the next tick.
     * @param name The name of the snake coming from the metadata.
     * @param data The new move data for this tick.
     */
    public void addPlayerMoveToQueue(String name, MoveData data) {
        tickLock.writeLock().lock();

        try {
            queuedMoves.put(name, data);
        } finally {
            tickLock.writeLock().unlock();
        }
    }

    /**
     * Handle each game tick that's scheduled for every 500ms by Spring.
     * Handles all the queued moves and sends out updated data.
     */
    @Scheduled(initialDelay = 250, fixedRate = 500)
    private void gameTick() {
        long tickStartTime = System.currentTimeMillis();

        // Lock write access to queuedMoves so that we can process all the moves.
        tickLock.writeLock().lock();

        try {
            // Get all current alive players for this tick, and map each to their new calculated head position
            Map<ServerSnake, Point> snakeMoves = entities.parallelStream()
                    .filter(Entity::isSnake).map(entity -> (ServerSnake) entity) // Get all snakes
                    .filter(Predicates.not(Snake::isDead)) // Find all alive snakes
                    .collect(Collectors.toMap(
                            snake -> snake, // Map key
                            snake -> { // New snake head calculated based on queuedMoves
                                MoveData move = queuedMoves.get(snake.getName());

                                // Update client alive status
                                if (move == null) {
                                    snake.incrementMissedTicks();
                                } else {
                                    snake.resetMissedTicks();
                                }

                                Direction facing = move != null
                                        ? move.getDirection()
                                        : snake.getFacing();

                                // Either we have move data for this tick or we have the direction its facing from an earlier tick
                                if (facing != null) {
                                    snake.setFacing(facing);

                                    // Apply the current move onto the snake head
                                    return new Point(
                                            snake.getHead().getX() + facing.x,
                                            snake.getHead().getY() + facing.y);
                                } else {
                                    // No move data for this tick nor facing from previous ticks.
                                    // Move away from the sides of the board just this once instead.
                                    Point head = snake.getHead();
                                    Direction newFacing = head.getX() > this.boardWidth / 2
                                            ? Direction.LEFT
                                            : Direction.RIGHT;

                                    snake.setFacing(newFacing);

                                    return new Point(
                                            head.getX() + newFacing.x,
                                            head.getY() + newFacing.y);
                                }
                            }
                    ));

            if (snakeMoves.isEmpty()) return;

            // Assign each snake to the position of its new head on the board
            snakeMoves.forEach((snake, newHead) -> {
                // Check if the new head is in board bounds
                if (newHead.isOnBoard(this.boardWidth, this.boardHeight)) {
                    // Assign this snake to the board square
                    BoardSquare square = board[newHead.getX()][newHead.getY()];
                    square.addOccupier(snake);
                } else {
                    // Kill this snake early because otherwise it won't be caught by
                    // the calculations inside each square
                    EntityModifier.setSnakeDead(snake, DeathReason.BOARD_COLLISION);
                }
            });

            // Allow each board square to perform calculations and kill/modify snakes
            // Collect all removed entities into one de-duplicated list.
            Set<Entity<?>> removedEntities = Stream.of(board).parallel()
                    .flatMap(column -> Stream.of(column).parallel()
                            .map(BoardSquare::process) // FIXME: the square is processed while snake tails have not yet been removed logically
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream))
                    .collect(Collectors.toSet());

            // Remove all dead snakes while visually preserving their new head position
            snakeMoves.forEach((snake, newHead) -> {
                // Remove the dead snake from all squares
                if (snake.isDead()) {
                    // TODO: clean this up
                    Point head = snake.getHead();
                    board[head.getX()][head.getY()].removeOccupier(snake);

                    for (Point p : snake.getBody()) {
                        board[p.getX()][p.getY()].removeOccupier(snake);
                    }

                    entities.remove(snake);
                    EntityModifier.moveSnake(snake, newHead);
                }
                // Check if the tail has changed, if so remove from board square
                else {
                    Point oldTail = snake.getTail();
                    EntityModifier.moveSnake(snake, newHead);

                    if (snake.getTail() != oldTail) {
                        board[oldTail.getX()][oldTail.getY()].removeOccupier(snake);
                    }
                }
            });

            // Count the eaten apples and add a new apple elsewhere on the board
            removedEntities.stream()
                    .filter(Entity::isApple)
                    .forEach((a) -> addApple());

            // Build the new tick data and send it out to all clients.
            TickData tick = TickData.Builder.builder()
                    .setEntities(entities.stream()
                            .map(Entity::toJsonData)
                            .collect(Collectors.toList()))
                    .setRemovedEntities(removedEntities.stream()
                            .map(Entity::toJsonData)
                            .collect(Collectors.toList()))
                    .build();
            websocket.sendTick(tick);

            // Clear all current moves since they have been processed.
            queuedMoves.clear();
        } finally {
            // Release lock on moves, allowing old moves from this tick or
            // new moves for the next tick to arrive.
            tickLock.writeLock().unlock();

            long totalTickTime = System.currentTimeMillis() - tickStartTime;
            logger.info("Game Tick time: {}ms", totalTickTime);

            printBoard();
        }
    }

    /**
     * Internal debugging utility for printing the board to the console in a pretty format.
     * Since the board is stored as (x,y) then we have to rotate it 90° and flip vertically.
     */
    private void printBoard() {
        StringBuffer buf = new StringBuffer((boardHeight + 1) * boardWidth);

        // Prefill with newlines for most of them to be replaced below
        for (int i = 0; i < buf.capacity(); i++) {
            buf.append('\n');
        }

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                int index = y * (boardWidth + 1) + x;
                char display = board[x][y].occupyingCount() > 0 ? 'X' : '.';
                buf.setCharAt(index, display);
            }
        }

        System.out.println(Strings.repeat("-", boardWidth));
        System.out.print(buf);
        System.out.println(Strings.repeat("-", boardWidth));
    }

    /**
     * Finds an open spot on the board and gets a random reward
     * value based on predefined apple rewards values with a probability,
     * then stores it to the board.
     */
    private void addApple() {
        Random rnd = new Random();
        int reward = 0, x = -1, y = -1;

        // Probabilities for each apple spawning
        // Apple value -> probability of spawning (0.0-1.0)
        double[] probabilities = {
                4, 0.05,
                3, 0.10,
                2, 0.15,
                1, 0.75,
        };

        // Generate a random reward value based on predefined probability values
        int rewardRandom = rnd.nextInt(1, 101);
        for (int i = 0; i < probabilities.length / 2; i++) {
            double probability = probabilities[i * 2 + 1];
            int inverseProbability = (int) ((1 - probability) * 100);

            if (rewardRandom >= inverseProbability) {
                reward = (int) probabilities[i * 2];
            }
        }

        // Find a random empty square on the board
        while (x < 0 || y < 0 || board[x][y].occupyingCount() >= 1) {
            x = rnd.nextInt(boardWidth);
            y = rnd.nextInt(boardHeight);
        }

        Apple apple = EntityModifier.newApple(new Point(x, y), reward);

        // Store the new apple
        entities.add(apple);
        board[x][y].addOccupier(apple);
    }
}
