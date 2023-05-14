package apcs.snakebattlearena.client;

import apcs.snakebattlearena.BoardInfo;
import apcs.snakebattlearena.commands.MoveCommand;
import apcs.snakebattlearena.entities.Apple;

public abstract class SnakeController {
    public abstract SnakeInfo onCreateSnake();

    public abstract MoveCommand onMove(BoardInfo board);

    public void onWorldCreated(BoardInfo board) {}

    public void onAppleCreated(Apple apple) {}

    public void onAppleDestroyed(Apple apple) {}

    public void onPlayerJoined(SnakeInfo snake) {}

    public void connect(String host) {
        // TODO: client connecting
    }
}
