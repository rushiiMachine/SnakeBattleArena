package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.models.entities.EntityData;

/**
 * A generic entity that belongs on the board.
 */
public interface Entity<T extends EntityData> {
    /**
     * Whether this entity can be collided with and cause a snake to die.
     */
    boolean isCollidable();

    /**
     * Utility method to check if this entity is a snake.
     */
    default boolean isSnake() {
        return this instanceof Snake;
    }

    /**
     * Utility method to check if this entity is a wall.
     */
    default boolean isWall() {
        return this instanceof Wall;
    }

    /**
     * Utility method to check if this entity is an apple.
     */
    default boolean isApple() {
        return this instanceof Apple;
    }

    /**
     * Serialize this entity into a JSON model ({@link T}).
     */
    T toJsonData();
}
