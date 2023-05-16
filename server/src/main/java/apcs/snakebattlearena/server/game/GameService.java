package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Apple;
import apcs.snakebattlearena.entities.Entity;
import apcs.snakebattlearena.entities.Snake;
import apcs.snakebattlearena.entities.Wall;
import apcs.snakebattlearena.models.MoveData;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@SuppressWarnings("unused")
public class GameService {
    // Queued moves between each tick to be processed at the next tick
    // Player name -> Latest received move data
    private final HashMap<String, MoveData> queuedMoves = new HashMap<>();
    private final ReentrantReadWriteLock queuedMovesLock = new ReentrantReadWriteLock();

    private final ArrayList<Entity> entities = new ArrayList<>();

    // Board with all available squares in terms of board[x][y]
    private final BoardSquare[][] board;
    private final int boardWidth, boardHeight;

    public GameService(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
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

    @Scheduled(fixedRate = 1000)
    private void gameTick() {
        queuedMovesLock.writeLock().lock();

        try {
            // Get all current players
            List<Snake> players = entities.stream()
                    .filter(entity -> entity instanceof Snake)
                    .map(entity -> (Snake) entity)
                    .filter(player -> !player.isDead())
                    .collect(Collectors.toList());

            // Prepare for selecting new dead players
            ArrayList<Snake> deadPlayers = new ArrayList<>(Math.min(5, players.size() / 4));

            // Loop over all players
            for (Snake player : players) {
                MoveData move = queuedMoves.get(player.getName());

                if (move != null) {
                    // Apply the current move onto the snake head
                    Point newHead = new Point(
                            player.getHead().getX() + move.getDirection().x,
                            player.getHead().getY() + move.getDirection().y);

                    player._internalMoveHead(newHead);

                    if (newHead.isOnBoard(boardWidth, boardHeight)) {
                        BoardSquare square = board[newHead.getX()][newHead.getY()];
                        square.addOccupier(player);
                    } else {
                        player._internalSetDead(true);
                        deadPlayers.add(player);
                    }
                } else {
                    // Client did not send a move event, automatically move towards
                    // the last direction moved or away from the walls.

                    // TODO: store last move direction
                }
            }

            entities.removeAll(deadPlayers);
            // TODO: kill all dead players

            // Clear all current moves.
            queuedMoves.clear();
        } finally {
            // Release lock on moves, allow any slow moves to still arrive,
            // or in time for the next tick.
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
            long invalidOverlappingCount = occupiers.stream()
                    .filter(e -> e instanceof Snake || e instanceof Wall)
                    .count();

            if (invalidOverlappingCount > 1) {
                // Overlapping snakes, mark them dead
                occupiers.stream()
                        .filter(e -> e instanceof Snake)
                        .forEach(e -> ((Snake) e)._internalSetDead(true));

                // Remove dead snakes & apples
                occupiers.removeIf(e -> e instanceof Apple || (e instanceof Snake && ((Snake) e).isDead()));
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
                apple._internalSetEaten(true);
                snake._internalAddCurledLength(apple.getReward());

                // Remove the apple
                occupiers.remove(apple);
            }
        }
    }
}
