package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;

public class Apple implements Entity {
    Point position;

    int reward;

    boolean eaten;

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

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }

    public int getReward() {
        return reward;
    }
}
