package apcs.snakebattlearena.commands;

import apcs.snakebattlearena.models.MoveData;

/**
 * Moves the snake in a certain direction upon a server tick.
 */
public class MoveCommand extends Command<MoveData> {
    private static final char[] VALID_DIRECTIONS = {'L', 'U', 'R', 'D'};

    private final char direction;

    /**
     * Command to move the snake in a certain direction by 1 square.
     * Note that there is nothing stopping you from moving back
     * onto yourself, making your snake instantly die.
     * @param direction One of 'L' (Left), 'U' (Up), 'R' (Right), 'D' (Down)
     */
    public MoveCommand(char direction) {
        this.direction = direction;

        for (char validDirection : VALID_DIRECTIONS) {
            if (validDirection == direction) break;
        }

        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

    /**
     * Internal websocket message identifier
     */
    @Override
    public String getId() {
        return "MOVE";
    }

    @Override
    public MoveData toJsonData() {
        MoveData.Direction direction;

        switch (this.direction) {
            case 'L': {
                direction = MoveData.Direction.LEFT;
                break;
            }
            case 'U': {
                direction = MoveData.Direction.UP;
                break;
            }
            case 'R': {
                direction = MoveData.Direction.RIGHT;
                break;
            }

            case 'D': {
                direction = MoveData.Direction.DOWN;
                break;
            }

            default: {
                throw new IllegalStateException("Invalid direction: " + this.direction);
            }
        }

        return MoveData.Builder.builder()
                .setDirection(direction)
                .build();
    }
}
