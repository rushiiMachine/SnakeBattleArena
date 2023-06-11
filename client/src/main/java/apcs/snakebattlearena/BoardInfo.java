package apcs.snakebattlearena;

/**
 * Information about the game board.
 */
public class BoardInfo {
    private final int width, height;

    /**
     * Internal method for representing board state.
     * @hidden
     */
    public BoardInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets this board's width in squares.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets this board's height in squares.
     */
    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardInfo)) return false;

        BoardInfo boardInfo = (BoardInfo) o;

        return width == boardInfo.width && height == boardInfo.height;
    }

    @Override
    public int hashCode() {
        return 31 * width + height;
    }
}
