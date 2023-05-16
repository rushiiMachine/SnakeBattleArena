package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;

@SuppressWarnings("unused")
public class Apple implements Entity {
    private final Point position;
    private final int reward;
    private boolean eaten;

    public Apple(Point position, int reward) {
        this.position = position;
        this.eaten = false;
        this.reward = reward;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void _internalSetEaten(boolean eaten) {
        this.eaten = eaten;
    }

    public int getReward() {
        return reward;
    }
}
