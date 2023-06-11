package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * A snake death reason.
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DeathReason {
    /**
     * Either a manual disconnect or loss of connection can trigger this death reason.
     */
    DISCONNECT,

    /**
     * A collision with a collidable entity, this has lower priority to {@link DeathReason#SELF_COLLISION}.
     */
    COLLISION,

    /**
     * A collision with yourself (overlapping parts of the body). This has higher precedence compared to
     * {@link DeathReason#COLLISION} so that if both types of collisions occur,
     * a self collision will be reported instead.
     */
    SELF_COLLISION,

    /**
     * A collision with the board (moved head is out of bounds).
     */
    BOARD_COLLISION,
}
