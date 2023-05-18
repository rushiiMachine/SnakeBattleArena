package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.entities.*;

import java.util.ArrayList;

/**
 * Represents a logical square on the board.
 * Note that what is shown visually may not represent what logically is happening.
 * i.e. Dead snakes survive visually n+1 ticks after they are marked dead and removed from the board.
 */
class BoardSquare {
    // Maximum possible entities is 6 because: 1 in this square, 4 corners moving in, and an apple or wall.
    private final ArrayList<Entity> occupiers = new ArrayList<>(6);

    /**
     * Add an occupying entity to this square logically.
     */
    public void addOccupier(Entity entity) {
        if (occupiers.size() >= 6) {
            throw new IllegalStateException("A square cannot have more than 6 occupiers at the same time!");
        }

        occupiers.add(entity);
    }

    /**
     * Removes an occupying entity from this square.
     *
     * @return true if this square contained the entity.
     */
    public boolean removeOccupier(Entity entity) {
        return occupiers.remove(entity);
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
