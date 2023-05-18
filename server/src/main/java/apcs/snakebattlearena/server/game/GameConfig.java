package apcs.snakebattlearena.server.game;

public class GameConfig {
    private final int boardWidth, boardHeight;

    public GameConfig(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
    }

    public int getBoardWidth() {
        return boardWidth;
    }

    public int getBoardHeight() {
        return boardHeight;
    }
}
