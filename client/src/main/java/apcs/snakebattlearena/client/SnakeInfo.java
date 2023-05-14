package apcs.snakebattlearena.client;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class SnakeInfo {
    private final String name;
    private final Color color;

    public SnakeInfo(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public SnakeInfo(String name) {
        Random rnd = new Random();
        int r = rnd.nextInt(256);
        int g = rnd.nextInt(256);
        int b = rnd.nextInt(256);

        this.name = name;
        this.color = new Color(r, g, b);
    }

    public String getName() {
        return name;
    }

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
