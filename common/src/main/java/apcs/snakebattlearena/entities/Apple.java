package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.entities.AppleData;

/**
 * An arbitrary apple that is only used once per client (recreated every lifecycle event)
 * or reused until disconnecting on the server side.
 */
@SuppressWarnings("unused")
public class Apple implements Entity<AppleData> {
    private final Point position;
    private final int reward;
    private boolean eaten;

    /**
     * Internal method for creating a new arbitrary apple.
     * You should not ever need to make a new apple yourself.
     */
    Apple(Point position, int reward) {
        this.position = position;
        this.eaten = false;
        this.reward = reward;
    }

    /**
     * Get the position of this apple.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Check if this apple is already eaten.
     * Note that this isn't necessary as apples disappear from
     * the public facing client API once they are eaten.
     */
    public boolean isEaten() {
        return eaten;
    }

    public int getReward() {
        return reward;
    }

    /**
     * Internal method to set this apple as eaten (unavailable).
     */
    void setAsEaten() {
        this.eaten = true;
    }

    @Override
    public boolean isCollidable() {
        return false;
    }

    @Override
    public AppleData toJsonData() {
        return AppleData.Builder.builder()
                .setPosition(position)
                .setReward(reward)
                .setEaten(eaten)
                .build();
    }
}
