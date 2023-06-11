package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum DeathReason {
    DISCONNECT,

    COLLISION,
    SELF_COLLISION,
    BOARD_COLLISION,
}
