package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;

import java.awt.*;

/**
 * A wrapper util for using internal entity methods by abusing
 * package-private in a separate module to avoid any users from
 * messing up internal state.
 */
public class EntityModifier {
    public static Snake newSnake(String name, Color color, Point initial) {
        return new Snake(name, color, initial);
    }

    public static void setSnakeDead(Snake snake, boolean dead) {
        snake.setDead(dead);
    }

    public static void moveSnakeHead(Snake snake, Point newHead) {
        snake.moveHead(newHead);
    }

    public static void addSnakeCurledLength(Snake snake, int amount) {
        snake.addCurledLength(amount);
    }

    public static Apple newApple(Point position, int reward) {
        return new Apple(position, reward);
    }

    public static void setAppleAsEaten(Apple apple) {
        apple.setAsEaten();
    }
}
