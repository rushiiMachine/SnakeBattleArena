package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.entities.WallData;

import java.util.Objects;

@SuppressWarnings("unused")
public class Wall implements Entity<WallData> {
    private final Point start, end;

    public Wall(Point start, Point end) {
        this.start = start;
        this.end = end;

        // Diagonal wall, invalid
        if (!isSingle() && !isVertical() && !isHorizontal()) {
            throw new IllegalArgumentException("Cannot create a diagonal wall");
        }
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }

    public boolean isVertical() {
        return !isSingle() && start.getX() == end.getX();
    }

    public boolean isHorizontal() {
        return !isSingle() && start.getY() == start.getY();
    }

    public boolean isSingle() {
        return Objects.equals(start, end);
    }

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
