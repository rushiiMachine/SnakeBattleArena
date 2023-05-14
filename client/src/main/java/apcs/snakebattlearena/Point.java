package apcs.snakebattlearena;

public final class Point {
    private final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Point[" + x + ", " + y + "]";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof Point
                && ((Point) o).x == x
                && ((Point) o).y == y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
