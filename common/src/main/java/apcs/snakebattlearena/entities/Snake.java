package apcs.snakebattlearena.entities;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.models.DeathReason;
import apcs.snakebattlearena.models.entities.SnakeData;
import apcs.snakebattlearena.models.entities.SnakeMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * An arbitrary snake that is only used once per client (recreated every lifecycle event)
 * or reused until client disconnects on the server side.
 */
@SuppressWarnings("unused")
public class Snake implements Entity<SnakeData> {
    // Metadata
    private final String name;
    private final Color color;

    // Position
    private final LinkedList<Point> body;
    private Point head;
    private int curledLength;

    // Other
    private DeathReason deathReason;

    /**
     * Internal method for creating a new arbitrary snake.
     * You should not ever need to make a new snake yourself.
     */
    Snake(@NotNull String name, @NotNull Color color, @NotNull Point head) {
        this.name = Objects.requireNonNull(name, "Cannot have snake with a null name!");
        this.color = Objects.requireNonNull(color, "Cannot have snake with a null color!");
        this.head = Objects.requireNonNull(head, "Cannot have snake with a null head point!");
        this.body = new LinkedList<>();
        this.curledLength = 0;
        this.deathReason = null;
    }

    /**
     * Gets the name of this snake
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Gets the color of this snake
     */
    @NotNull
    public Color getColor() {
        return color;
    }

    /**
     * Get the position of this snakes head
     */
    @NotNull
    public Point getHead() {
        return head;
    }

    /**
     * Get the entire snake body's positions ordered by closest to head (1st) -> tail (last).
     * Note that the head AND curled tail is <b>excluded</b> from the returning positions.
     */
    @NotNull
    public List<Point> getBody() {
        return Collections.unmodifiableList(body);
    }

    /**
     * Gets any last part of the snake body. Can be either
     * the last part of the body or head if none.
     */
    @NotNull
    public Point getTail() {
        return body.isEmpty() ? head : body.getLast();
    }

    /**
     * Get the <b>full</b> length of this snake, including the body, head,
     * and curled tail length! (refer to {@link Snake#getCurledLength()}
     */
    public int getLength() {
        return body.size() + 1 + curledLength;
    }

    /**
     * Get the curled tail length, where the snake tail contains is curled up into one square,
     * only happening when the snake eats an apple with a reward of greater than 1.
     * <p>
     * The tail uncurls by 1 for every new move the snake does,
     * until there is no more curled tail and the tail starts actually moving.
     */
    public int getCurledLength() {
        return curledLength;
    }

    /**
     * Checks whether this snake is dead.
     * <p>
     * If dead, then it does not provide a physical obstruction to others,
     * and you can go through it without dying yourself. Considering a dead snake
     * is still sent to clients for visual, this is important to check when deciding
     * the optimal path.
     */
    public boolean isDead() {
        return deathReason != null;
    }

    /**
     * Internal method for setting the snake death state.
     */
    void setDead(@Nullable DeathReason deathReason) {
        this.deathReason = deathReason;
    }

    /**
     * Get the current death reason if this snake is dead,
     * otherwise null. Refer to {@link Snake#isDead()} for more information.
     */
    public DeathReason getDeathReason() {
        return deathReason;
    }

    /**
     * Internal method for moving the head and subsequently shifting
     * the old head into the body and uncurling the tail.
     */
    void moveSnake(@NotNull Point newHead) {
        boolean hasBody = !body.isEmpty();

        if (curledLength <= 0 && hasBody) {
            body.removeLast();
            body.addFirst(head);
        }

        if (curledLength > 0) {
            curledLength--;
            body.addFirst(head);
        }

        head = newHead;
    }

    /**
     * Internal method for increasing the curled tail length.
     */
    void addCurledLength(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot shrink curled length with a negative number!");
        }

        this.curledLength += amount;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public SnakeData toJsonData() {
        return SnakeData.Builder.builder()
                .setMetadata(SnakeMetadata.Builder.builder()
                        .setName(name)
                        .setColor(color)
                        .build())
                .setBody(getBody())
                .setHead(head)
                .setCurledLength(curledLength)
                .build();
    }
}
