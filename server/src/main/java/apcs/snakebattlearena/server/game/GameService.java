package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.*;
import apcs.snakebattlearena.models.MoveData;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    //    public GameService(int boardWidth, int boardHeight) {
    public GameService() {
        this.boardWidth = 25;
        this.boardHeight = 25;
        this.board = new BoardSquare[boardWidth][boardHeight];
    }

    public void movePlayer(String name, MoveData data) {
        queuedMovesLock.writeLock().lock();

        try {
            queuedMoves.put(name, data);
        } finally {
            queuedMovesLock.writeLock().unlock();
        }
    }

    @Scheduled(initialDelay = 1000, fixedRate = 5000)
    private void gameTick() {
        System.out.println("tick");
        // Lock write access to queuedMoves so that we can process all the moves.
        queuedMovesLock.writeLock().lock();

        try {
            // Get all current alive players for this tick, and map each to their new calculated head position
            Map<Snake, Point> snakeMoves = entities.parallelStream()
                    .filter(entity -> entity instanceof Snake && !((Snake) entity).isDead()) // Find all alive snakes
                    .map(entity -> (Snake) entity)
                    .collect(Collectors.toMap(
                            snake -> snake, // Map key
                            snake -> { // New snake head calculated based on queuedMoves
                                MoveData move = queuedMoves.get(snake.getName());

                                if (move != null) {
                                    // Apply the current move onto the snake head
                                    return new Point(
                                            snake.getHead().getX() + move.getDirection().x,
                                            snake.getHead().getY() + move.getDirection().y);
                                } else {
                                    // Client did not send a move event, automatically move towards
                                    // the last direction moved or away from the walls.
                                    // TODO: use last moved direction
                                    return new Point(snake.getHead().getX() + 1, snake.getHead().getY());
                                }
                            }
                    ));

            // Assign each new head to the square on the board
            snakeMoves.forEach((snake, newHead) -> {
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
            for (BoardSquare[] row : board) {
                for (BoardSquare square : row) {
                    square.markOverlappingDead();
                }
            }

            // Remove all dead snakes while visually preserving their new head position
            snakeMoves.forEach((snake, newHead) -> {
                EntityModifier.moveSnakeHead(snake, newHead);

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
    }

    /**
     * Represents a logical square on the board.
     * Note that what is shown visually may not represent what logically is happening.
     * i.e. Dead snakes survive visually n+1 ticks after they are marked dead and removed from the board.
     */
    private static class BoardSquare {
        // Maximum possible entities is 6 because: 1 @ self, 4 corners moving in, and an apple or wall.
        private final ArrayList<Entity> occupiers = new ArrayList<>(6);

        /**
         * Add an occupying entity to this square logically.
         */
        public void addOccupier(Entity entity) {
            if (occupiers.size() >= 5) {
                throw new IllegalStateException("A square cannot have more than 5 occupiers at the same time!");
            }

            occupiers.add(entity);
        }

        /**
         * Mark any overlapping snakes dead, mark apples eaten, and
         * remove both as occupiers of this square <em>logically.</em>
         */
        public void markOverlappingDead() {
            // No overlapping entities
            if (occupiers.size() <= 1) return;

            // Get amount of snakes and walls in this square
            long overlappableEntities = occupiers.stream()
                    .filter(e -> e instanceof Snake || e instanceof Wall)
                    .count();

            if (overlappableEntities > 1) {
                // Overlapping snakes, mark them dead
                occupiers.stream()
                        .filter(e -> e instanceof Snake)
                        .forEach(s -> EntityModifier.setSnakeDead((Snake) s, true));

                // Remove dead snakes & apples
                occupiers.removeIf(e -> e instanceof Apple
                        || (e instanceof Snake && ((Snake) e).isDead()));
            } else {
                // Just snake and an apple are left
                Apple apple = (Apple) occupiers.stream()
                        .filter(e -> e instanceof Apple)
                        .findFirst().orElse(null);

                Snake snake = (Snake) occupiers.stream()
                        .filter(e -> e instanceof Snake)
                        .findFirst().orElse(null);

                if (apple == null || snake == null) return;

                // Apply effects to snake
                EntityModifier.setAppleAsEaten(apple);
                EntityModifier.addSnakeCurledLength(snake, apple.getReward());

                // Remove the apple
                occupiers.remove(apple);
            }
        }
    }
}
