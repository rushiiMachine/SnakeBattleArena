package apcs.snakebattlearena.server.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Snake;
import apcs.snakebattlearena.models.Direction;
import apcs.snakebattlearena.models.entities.SnakeMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A snake used only for the server to store additional properties.
 */
public class ServerSnake extends Snake {
    private final String id;
    private Direction facing = null;
    private int missedTicks = 0;

    /**
     * Create a server side snake with additional data
     * @param id       The unique ID for the websocket connection.
     * @param metadata Initializing metadata of this player
     * @param initial  The initial head position of this snake.
     */
    public ServerSnake(@NotNull String id,
                       @NotNull SnakeMetadata metadata,
                       @NotNull Point initial) {
        super(metadata.getName(), metadata.getColor(), initial);
        this.id = id;
    }

    /**
     * Gets the unique ID for this snake associated to the websocket connection.
     */
    @NotNull
    public String getId() {
        return id;
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

    /**
     * Check if the amount of missed ticks is low enough that
     * it could've been caused by a temporary connection drop.
     * If not, then most likely the connection has been silently
     * dropped, or the client is malfunctioning.
     */
    public boolean isClientAlive() {
        return missedTicks <= 6;
    }

    /**
     * Reset the missed ticks as the client has sent new data.
     */
    public void resetMissedTicks() {
        missedTicks = 0;
    }

    /**
     * Increase the counter for missed move data ticks.
     * Refer to {@link ServerSnake#isClientAlive()} for more information.
     */
    public void incrementMissedTicks() {
        missedTicks++;
    }

    /**
     * Get the amount of sequential ticks missed.
     * Refer to {@link ServerSnake#isClientAlive()} for more information.
     */
    public int getMissedTickCount() {
        return missedTicks;
    }
}
