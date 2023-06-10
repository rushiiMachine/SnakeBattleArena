package apcs.snakebattlearena.models;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Returned errors upon attempting to join the game (/join)
 */
@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum JoinError {
    /**
     * Returned when there is already another player with the same name.
     */
    PLAYER_EXISTS,

    /**
     * Returned when the session has already been registered as a
     * player and cannot register again.
     */
    INVALID_SESSION,

    /**
     * Returned when the supplied client version does not match the
     * major & minor SemVer versions of the server.
     */
    VERSION_MISMATCH,
}
