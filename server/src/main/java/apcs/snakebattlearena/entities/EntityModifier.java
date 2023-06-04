package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.DeathReason;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public static void setSnakeDead(@NotNull Snake snake,
                                    @Nullable DeathReason deathReason) {
        snake.setDead(deathReason);
    }

    public static void moveSnake(@NotNull Snake snake, Point newHead) {
        snake.moveSnake(newHead);
    }

    public static void addSnakeCurledLength(@NotNull Snake snake, int amount) {
        snake.addCurledLength(amount);
    }

    public static Apple newApple(@NotNull Point position, int reward) {
        return new Apple(position, reward);
    }

    public static void setAppleAsEaten(@NotNull Apple apple) {
        apple.setAsEaten();
    }
}
