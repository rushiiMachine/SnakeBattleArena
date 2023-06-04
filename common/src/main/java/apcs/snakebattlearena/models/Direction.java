package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Direction {
    LEFT(-1, 0),
    UP(0, -1),
    RIGHT(1, 0),
    DOWN(0, 1);

    /**
     * Positive or negative coordinate difference.
     */
    public final int x, y;

    Direction(int xDiff, int yDiff) {
        this.x = xDiff;
        this.y = yDiff;
    }
}
