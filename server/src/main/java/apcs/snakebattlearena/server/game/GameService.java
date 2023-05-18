package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Entity;
import apcs.snakebattlearena.entities.EntityModifier;
import apcs.snakebattlearena.entities.ServerSnake;
import apcs.snakebattlearena.entities.Snake;
import apcs.snakebattlearena.models.Direction;
import apcs.snakebattlearena.models.MoveData;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Handles all the game logic at a fixed tick rate.
 */
@Service
@EnableScheduling
@SuppressWarnings("unused")
public class GameService {
    // Queued moves between each tick to be processed at the next tick
    // Player name -> Latest received move data
    private final HashMap<String, MoveData> queuedMoves = new HashMap<>();
    private final ReentrantReadWriteLock queuedMovesLock = new ReentrantReadWriteLock();

    // All entities on this board (snake, apple, wall, etc...)
    private final HashSet<Entity> entities = new HashSet<>();

    // Board with all available squares in terms of board[x][y]
    private final BoardSquare[][] board;
    private final int boardWidth, boardHeight;

    public GameService(GameConfig config) {
        this.boardWidth = config.getBoardWidth();
        this.boardHeight = config.getBoardHeight();

        // Initialize board
        this.board = new BoardSquare[boardWidth][boardHeight];
        Arrays.parallelSetAll(this.board, i -> new BoardSquare[boardHeight]);
        for (BoardSquare[] column : this.board) {
            Arrays.parallelSetAll(column, i -> new BoardSquare());
        }
    }

    public void movePlayer(String name, MoveData data) {
        queuedMovesLock.writeLock().lock();

        try {
            queuedMoves.put(name, data);
        } finally {
            queuedMovesLock.writeLock().unlock();
        }
    }

    /**
     * Handle each game tick that's scheduled for every 500ms by Spring.
     * Handle all the queued moves and send out updated data.
     */
    @Scheduled(initialDelay = 250, fixedRate = 5000)
    private void gameTick() {
        long time = System.currentTimeMillis();

        // Lock write access to queuedMoves so that we can process all the moves.
        queuedMovesLock.writeLock().lock();

        try {
            // Get all current alive players for this tick, and map each to their new calculated head position
            Map<@NotNull ServerSnake, @NotNull Point> snakeMoves = entities.parallelStream()
                    .filter(entity -> entity instanceof ServerSnake && !((Snake) entity).isDead()) // Find all alive snakes
                    .map(entity -> (ServerSnake) entity)
                    .collect(Collectors.toMap(
                            snake -> snake, // Map key
                            snake -> { // New snake head calculated based on queuedMoves
                                MoveData move = queuedMoves.get(snake.getName());
                                Direction facing = move != null ? move.getDirection() : snake.getFacing();

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
                    EntityModifier.setSnakeDead(snake, true);
                }
            });

            // Allow each board square to perform calculations and kill/modify snakes
            for (BoardSquare[] col : board) {
                for (BoardSquare square : col) {
                    square.markOverlappingDead();
                }
            }

            // Remove all dead snakes while visually preserving their new head position
            snakeMoves.forEach((snake, newHead) -> {
                Point oldTail = snake.getTail();
                EntityModifier.moveSnakeHead(snake, newHead);

                // Check if the tail has changed, if so remove from board square
                if (oldTail != null && snake.getTail() != oldTail) {
                    board[oldTail.getX()][oldTail.getY()].removeOccupier(snake);
                }

                if (snake.isDead()) {
                    entities.remove(snake);
                }
            });

            // TODO: send out movement data for all snakes

            // Clear all current moves since they have been processed.
            queuedMoves.clear();
        } finally {
            // Release lock on moves, allowing old moves from this tick or
            // new moves for the next tick to arrive.
            queuedMovesLock.writeLock().unlock();
        }

        System.out.println("tick: " + (System.currentTimeMillis() - time) + "ms");
    }
}
