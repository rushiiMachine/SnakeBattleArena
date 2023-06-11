package apcs.snakebattlearena;

public class BoardInfo {
    private final int width, height;

    public BoardInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

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
