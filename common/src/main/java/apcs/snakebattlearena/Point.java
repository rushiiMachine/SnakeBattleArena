package apcs.snakebattlearena;

/**
 * Represents a 2D (x,y) point on the game board.
 */
public final class Point {
    private final int x, y;

    /**
     * Create an arbitrary point that may correspond to one on the board.
     * Note that the board is organized in terms of (0,0) being the top left.
     *
     * <pre>
     *      <b>(0,0)</b>      (1,0)      (2,0)
     *            +------+------+
     *            |      |      |
     *      (1,0) |------+------+ (2,1)
     *            |      |      |
     *            +------+------+
     *      (2,0)      (1,2)      (2,2)
     * </pre>
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x coordinate of this point
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate of this point
     */
    public int getY() {
        return y;
    }

    /**
     * Checks if this point is on the board.
     *
     * @param boardWidth The game board width
     * @param boardHeight The game board height
     */
    public boolean isOnBoard(int boardWidth, int boardHeight) {
        return this.x >= 0 && this.x < boardWidth &&
                this.y >= 0 && this.y < boardHeight;
    }

    /**
     * Checks if this point is equal to the supplied xy coordinates
     * without creating a new {@link Point} instance.
     */
    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Point
                && ((Point) o).x == x
                && ((Point) o).y == y;
    }

    @Override
    public String toString() {
        return "Point[" + x + ", " + y + "]";
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
