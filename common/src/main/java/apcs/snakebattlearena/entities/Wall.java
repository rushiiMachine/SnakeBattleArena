package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.entities.WallData;

import java.util.Objects;

/**
 * A physical straight barrier that cannot be moved.
 * A wall is represented by two points (inclusive) between which a wall stretches.
 * Use {@link Wall#getStart()} and {@link Wall#getEnd()} to get the boundary of the wall,
 * or {@link Wall#isPointInWall(Point)} to check if a spot is inside the wall.
 */
@SuppressWarnings("unused")
public class Wall implements Entity<WallData> {
    private final Point start, end;

    /**
     * Internal constructor for making a wall.
     * @hidden
     */
    public Wall(Point start, Point end) {
        this.start = start;
        this.end = end;

        // Diagonal wall, invalid
        if (!isSingle() && !isVertical() && !isHorizontal()) {
            throw new IllegalArgumentException("Cannot create a diagonal wall");
        }
    }

    /**
     * Gets the start coordinate for the wall (inclusive).
     * The name "start" has no special significance.
     */
    public Point getStart() {
        return start;
    }

    /**
     * Gets the end coordinate for the wall (inclusive).
     * The name "end" has no special significance.
     */
    public Point getEnd() {
        return end;
    }

    /**
     * Check if this wall stretches vertically.
     * Note that if this wall is a single point ({@link Wall#isSingle()}) then this returns false.
     */
    public boolean isVertical() {
        return !isSingle() && start.getX() == end.getX();
    }

    /**
     * Check if this wall stretches horizontally.
     * Note that if this wall is a single point ({@link Wall#isSingle()}) then this returns false.
     */
    public boolean isHorizontal() {
        return !isSingle() && start.getY() == start.getY();
    }

    /**
     * Check if this wall is a single point (start and end coordinate is the same).
     */
    public boolean isSingle() {
        return Objects.equals(start, end);
    }

    /**
     * Checks if a point is in inside this wall.
     * @param point The point on the board.
     */
    public boolean isPointInWall(Point point) {
        if (point == null) return false;

        if (start == point || end == point)
            return true;

        int a, b, c;

        if (isVertical() && start.getX() == point.getX()) {
            a = start.getY();
            b = end.getY();
            c = point.getY();
        } else if (isHorizontal() && start.getY() == point.getY()) {
            a = start.getX();
            b = end.getX();
            c = point.getX();
        } else {
            // Point not even extrapolated
            return false;
        }

        // Check if distance from both sides of point adds up to the line length
        return Math.abs(a - c) + Math.abs(b - c) == Math.abs(a - b);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public WallData toJsonData() {
        return WallData.Builder.builder()
                .setStart(start)
                .setEnd(end)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wall)) return false;

        Wall wall = (Wall) o;

        return Objects.equals(start, wall.start)
                && Objects.equals(end, wall.end);
    }

    @Override
    public int hashCode() {
        int result = start != null ? start.hashCode() : 0;
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }
}
