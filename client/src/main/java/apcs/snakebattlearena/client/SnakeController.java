package apcs.snakebattlearena.client;

import apcs.snakebattlearena.BoardInfo;
import apcs.snakebattlearena.commands.MoveCommand;
import apcs.snakebattlearena.entities.Apple;
import apcs.snakebattlearena.models.SnakeData;

import java.util.Objects;

/**
 * A client that listens to server events and controls its own snake to move.
 * Call {@link SnakeController#connect(String)} to connect to a server.
 */
@SuppressWarnings("unused")
public abstract class SnakeController {
    /**
     * Should override to return information about your snake to be used when
     * connecting to the server. This is only called once and is unchangeable.
     *
     * @return A Not-null, valid {@link SnakeInfo}
     */
    public abstract SnakeInfo getSnakeInfo();

    /**
     * Called every server tick when it is time to move your snake.
     * You should override this and return a move that corresponds to the best move
     * with the current state of the board. Note that unless collisions are disabled,
     * you can crash into other snakes and entities that are in your path! You cannot
     * predict the exact square other snakes will move to until the next tick, meaning
     * would need to avoid, if possible, those possible squares.
     *
     * @param board The state of the board at the current tick.
     * @return A non-null {@link MoveCommand} that moves your snake.
     */
    public abstract MoveCommand onMove(BoardInfo board);

    /**
     * Called when you die from a collision with another entity or yourself.
     *
     * @param board The state of the board at the current tick.
     */
    public void onDeath(BoardInfo board) {}

    /**
     * Called when you respawn after a death. Note that if there's a respawn delay,
     * this may not be called instantly after {@link SnakeController#onDeath(BoardInfo)}.
     *
     * @param board The state of the board at the current tick.
     */
    public void onRespawn(BoardInfo board) {}

    /**
     * Called when the board is initialized (after connecting) or wiped clean.
     * The board may change size or other attributes during such an event.
     *
     * @param board The new board info. No entities will be present on it.
     */
    public void onWorldCreated(BoardInfo board) {}

    /**
     * Called when a new apple is created on the board.
     *
     * @param apple The new apple
     */
    public void onAppleCreated(Apple apple) {}

    /**
     * Called when an apple disappears from the board or is
     * eaten by another player.
     *
     * @param apple The same instance of the apple, but
     *              {@link Apple#isEaten()} is now true.
     */
    public void onAppleDestroyed(Apple apple) {}

    /**
     * Called when a new snake joins the server.
     *
     * @param snake The information of the new snake.
     */
    public void onPlayerJoined(SnakeInfo snake) {}

    /**
     * Called when a snake leaves the server.
     *
     * @param snake The information of the old snake.
     */
    public void onPlayerLeave(SnakeInfo snake) {}

    /**
     * Connect to the server and run a blocking client for this snake.
     *
     * @param host Any valid URI pointing to the server's websocket.
     *             This can be an IP/Domain, w/ or w/o a port (defaulting to port 80).
     */
    public final void connect(String host) {
        Objects.requireNonNull(host, "Target host cannot be null!");

        SnakeInfo snake = getSnakeInfo();
        SnakeData data = SnakeData.Builder.builder()
                .setName(snake.getName())
                .setColor(snake.getColor())
                .build();

        // TODO: client connecting
    }
}
