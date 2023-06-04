package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.entities.Apple;
import apcs.snakebattlearena.entities.Entity;
import apcs.snakebattlearena.entities.EntityModifier;
import apcs.snakebattlearena.entities.Snake;
import apcs.snakebattlearena.models.DeathReason;
import apcs.snakebattlearena.utils.Predicates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a logical square on the board.
 * Note that what is shown visually may not represent what logically is happening.
 * i.e. Dead snakes survive visually n+1 ticks after they are marked dead and removed from the board.
 */
class BoardSquare {
    // Maximum possible entities is 6 because: 1 in this square, 4 corners moving in, and an apple or wall.
    private final ArrayList<Entity<?>> occupiers = new ArrayList<>(6);

    /**
     * Returns the amount of logical entities registered to this square.
     */
    public int occupyingCount() {
        return occupiers.size();
    }

    /**
     * Add an occupying entity to this square logically.
     */
    public void addOccupier(Entity<?> entity) {
        if (occupiers.size() >= 6) {
            throw new IllegalStateException("A square cannot have more than 6 occupiers at the same time!");
        }

        occupiers.add(entity);
    }

    /**
     * Removes an occupying entity from this square.
     * @return true if this square contained the entity.
     */
    public boolean removeOccupier(Entity<?> entity) {
        return occupiers.remove(entity);
    }

    /**
     * Mark any overlapping snakes dead, mark apples eaten, and
     * remove both as occupiers of this square <em>logically.</em>
     * @return All entities that were removed from this square or null if none.
     */
    public Set<Entity<?>> process() {
        // No overlapping entities
        if (occupiers.size() <= 1) return null;

        // Get amount of snakes and walls in this square
        long collidableEntities = occupiers.stream()
                .filter(Entity::isCollidable)
                .count();

        // Keep track of all the entities that have been removed.
        HashSet<Entity<?>> removedEntities = new HashSet<>((int) collidableEntities);

        // Mark all snakes in this square dead since there's more than 1 collidable entity.
        if (collidableEntities > 1) {
            occupiers.stream()
                    .filter(Entity::isSnake).map(e -> (Snake) e)
                    .filter(Predicates.not(Snake::isDead))
                    .forEach(snake -> {
                        // If a snake is already marked to be removed, then that means
                        // it's body is in this square twice, and as such is a self-collision.
                        DeathReason reason = removedEntities.add(snake)
                                ? DeathReason.SNAKE_COLLISION
                                : DeathReason.SELF_COLLISION;
                        EntityModifier.setSnakeDead(snake, reason);
                    });
        } else {
            // Just snake and an apple are left
            Apple apple = (Apple) occupiers.stream()
                    .filter(Entity::isApple)
                    .findFirst().orElse(null);

            Snake snake = (Snake) occupiers.stream()
                    .filter(Entity::isSnake)
                    .findFirst().orElse(null);

            if (apple == null || snake == null)
                return null;

            // Apply apple's effects to snake
            EntityModifier.setAppleAsEaten(apple);
            EntityModifier.addSnakeCurledLength(snake, apple.getReward());

            removedEntities.add(apple);
        }

        // Remove all apples and dead snakes
        occupiers.removeIf(e -> e.isApple()
                || (e.isSnake() && ((Snake) e).isDead()));

        return removedEntities;
    }
}
