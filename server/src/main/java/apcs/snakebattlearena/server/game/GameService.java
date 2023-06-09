package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Entity;
import apcs.snakebattlearena.server.entities.ServerSnake;
import apcs.snakebattlearena.models.*;
import apcs.snakebattlearena.models.entities.SnakeMetadata;
import apcs.snakebattlearena.server.websocket.WebsocketSender;
import apcs.snakebattlearena.server.websocket.WebsocketUserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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
     * The board containing all the collision logic and squares.
     */
    private final Board board;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebsocketSender websocket;

    @Autowired
    private WebsocketUserManager websocketUsers;

    public GameService(GameConfig config) {
        this.board = new Board(config.getBoardWidth(), config.getBoardHeight());

        // Pre-generate apples
        for (int i = 0; i < config.getAppleCount(); i++) {
            entities.add(board.generateNewApple());
        }

        board.printBoardToConsole();
    }

    /**
     * Adds a new player to the board.
     * @param id        A unique ID for this player.
     * @param snakeData The metadata about the new snake.
     * @return A join error if unsuccessful otherwise empty.
     */
    public Optional<JoinError> addPlayer(String id, SnakeMetadata snakeData) {
        // Lock write access to entities
        tickLock.writeLock().lock();

        try {
            JoinError err = null;

            // Check if the player is already on this board based on name or ID
            for (Entity<?> e : entities) {
                if (!(e instanceof ServerSnake))
                    continue;

                ServerSnake snake = (ServerSnake) e;

                if (snake.getId().equals(id)) {
                    err = JoinError.INVALID_SESSION;
                    break;
                }

                if (snake.getName().equals(snakeData.getName())) {
                    err = JoinError.PLAYER_EXISTS;
                    break;
                }
            }

            // Return error if any
            if (err != null) {
                return Optional.of(err);
            }

            logger.info("A new player {} has joined the game!", snakeData.getName());

            // Store the new snake
            ServerSnake snake = new ServerSnake(id, snakeData, board.getRandomPoint());
            entities.add(snake);

            return Optional.empty();
        } finally {
            // Remove the lock on entities
            tickLock.writeLock().unlock();
        }
    }

    /**
     * Mark a player dead to be removed at the next tick, and disconnect their websocket connection.
     * @param id The snake's unique ID.
     * @return True if the player exists and has been removed otherwise false.
     */
    public boolean removePlayer(String id) {
        // Lock access to entities
        tickLock.writeLock().lock();

        try {
            // Find the snake by id
            ServerSnake snake = entities.stream()
                    .filter(Entity::isSnake).map(e -> (ServerSnake) e)
                    .filter(s -> s.getId().equals(id))
                    .findFirst().orElse(null);

            if (snake == null)
                return false;

            logger.info("Player {} has left the game!", snake.getName());

            // Kill the snake to be removed at the next tick
            snake.internalSetDead(DeathReason.DISCONNECT);
            return true;
        } finally {
            // Unlock entities
            tickLock.writeLock().unlock();
        }
    }

    /**
     * Queues up a player move to be processed at the next tick.
     * @param name The name of the snake coming from the metadata.
     * @param data The new move data for this tick.
     */
    public void addPlayerMoveToQueue(String name, MoveData data) {
        // Lock write access to queuedMoves
        tickLock.writeLock().lock();

        try {
            queuedMoves.put(name, data);
        } finally {
            // Unlock queuedMoves
            tickLock.writeLock().unlock();
        }
    }

    /**
     * Handle each game tick that's scheduled for every 500ms by Spring.
     * Handles all the queued moves and sends out updated data.
     */
    @Scheduled(initialDelay = 250, fixedRate = 5000)
    private void gameTick() {
        long tickStartTime = System.currentTimeMillis();

        // Lock write access to queuedMoves so that we can process all the moves.
        tickLock.writeLock().lock();

        try {
            // Get all current alive players for this tick, and map each to their new calculated head position
            Map<ServerSnake, Point> snakeMoves = entities.parallelStream()
                    .filter(Entity::isSnake).map(entity -> (ServerSnake) entity) // Get all snakes
                    .filter(s -> !s.isDead()) // Filter out dead snakes
                    .collect(Collectors.toMap(
                            snake -> snake, // Map key
                            snake -> { // New snake head calculated based on queuedMoves
                                MoveData move = queuedMoves.get(snake.getName());

                                // Update client alive status
                                if (move == null && !snake.isClientAlive()) {
                                    logger.info("Player \"{}\" has missed a large amount of ticks, disconnecting...", snake.getName());
                                    snake.internalSetDead(DeathReason.DISCONNECT);
                                    websocketUsers.disconnectUser(snake.getId());
                                } else if (move == null) {
                                    snake.incrementMissedTicks();
                                    logger.info("Player \"{}\" has missed a tick! ({})", snake.getName(), snake.getMissedTickCount());
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
                                    Direction newFacing = head.getX() > board.getBoardWidth() / 2
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

            // Update the logical positions of this snake on the board
            snakeMoves.forEach((snake, newHead) -> {
                // Check if the new head is in board bounds
                if (board.isPointOnBoard(newHead)) {
                    // Move the head on the snake itself
                    Point oldTail = snake.getTail();
                    snake.internalMove(newHead);

                    // If the tail has moved, then remove the old tail from the old square
                    if (!snake.getTail().equals(oldTail)) {
                        Objects.requireNonNull(board.getSquare(oldTail)).removeOccupier(snake);
                    }

                    // Assign this snake to its new head square
                    Objects.requireNonNull(board.getSquare(newHead)).addOccupier(snake);
                } else {
                    // Kill this snake early because otherwise it won't be caught by
                    // the calculations inside each square
                    snake.internalSetDead(DeathReason.BOARD_COLLISION);
                }
            });

            // Allow each board square to perform calculations and kill/modify snakes
            // Collect all removed entities into one de-duplicated list.
            // TODO: move this to Board(?)
            Set<Entity<?>> removedEntities = Stream.of(board.getBoard()).parallel()
                    .flatMap(column -> Stream.of(column).parallel()
                            .map(BoardSquare::process)
                            .filter(Objects::nonNull)
                            .flatMap(Collection::stream))
                    .collect(Collectors.toSet());

            // Remove all dead snakes while visually preserving their new head position
            // TODO: each square process already removes dead snakes from their own square if they've already been marked dead, probably should only do it once here instead
            snakeMoves.forEach((snake, newHead) -> {
                if (snake.isDead()) {
                    // Remove all parts of the snake from the board
                    Objects.requireNonNull(board.getSquare(snake.getHead())).removeOccupier(snake);

                    for (Point body : snake.getBody()) {
                        Objects.requireNonNull(board.getSquare(body)).removeOccupier(snake);
                    }

                    entities.remove(snake);
                    removedEntities.add(snake);
                }
            });

            // Count the eaten apples and add a new apple elsewhere on the board
            removedEntities.stream()
                    .filter(Entity::isApple)
                    .map((a) -> board.generateNewApple())
                    .forEach(entities::add); // Store the new apple to entities

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

            // Log the tick time
            long totalTickTime = System.currentTimeMillis() - tickStartTime;
            logger.debug("Game Tick time: {}ms", totalTickTime);

            board.printBoardToConsole();
        }
    }
}
