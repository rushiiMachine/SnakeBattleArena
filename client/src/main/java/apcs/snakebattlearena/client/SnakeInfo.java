package apcs.snakebattlearena.client;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Information about your Snake upon connecting to the server.
 * This should be returned in an override of {@link SnakeController#getSnakeInfo()}.
 */
public class SnakeInfo {
    private final String name;
    private final Color color;

    /**
     * Create a snake with a name and color
     */
    public SnakeInfo(String name, Color color) {
        this.name = Objects.requireNonNull(name, "Snake name cannot be null");
        this.color = Objects.requireNonNull(color, "Snake color cannot be null");
    }

    /**
     * Create a snake with a name and a random color
     */
    public SnakeInfo(String name) {
        Random rnd = new Random();
        int r = rnd.nextInt(256);
        int g = rnd.nextInt(256);
        int b = rnd.nextInt(256);

        this.name = Objects.requireNonNull(name, "Snake name cannot be null");
        this.color = new Color(r, g, b);
    }

    /**
     * Get this snake's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get this snake's color
     */
    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SnakeInfo
                && Objects.equals(name, ((SnakeInfo) o).name)
                && Objects.equals(color, ((SnakeInfo) o).color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
