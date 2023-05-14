package apcs.snakebattlearena.commands;

/**
 * A generic command that a snake can send to the server.
 * @param <T> The return type of {@link Command#toJsonData()}
 */
public abstract class Command<T> {
    /**
     * Internal websocket message identifier.
     * Sent to {@code /app/commands/<ID>}
     */
    public abstract String getId();

    /**
     * Converts this command into a serializable object to be sent to the server.
     * @return A JSON serializable object
     */
    public abstract T toJsonData();
}
