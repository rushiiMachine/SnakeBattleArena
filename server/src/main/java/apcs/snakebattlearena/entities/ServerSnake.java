package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * A snake used only for the server to store additional properties.
 */
public class ServerSnake extends Snake {
    private Direction facing = null;

    public ServerSnake(@NotNull String name, @NotNull Color color, @NotNull Point initial) {
        super(name, color, initial);
    }

    /**
     * Stores the last sent move data to preserve the facing direction in case additional
     * move data isn't received on time so that the snake can move automatically forward.
     */
    @Nullable
    public Direction getFacing() {
        return facing;
    }

    /**
     * Sets the last move direction (not null)
     */
    public void setFacing(@NotNull Direction lastMovedDirection) {
        this.facing = lastMovedDirection;
    }
}
