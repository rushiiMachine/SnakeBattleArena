package apcs.snakebattlearena.commands;

import apcs.snakebattlearena.models.MoveData;

public class MoveCommand extends Command<MoveData> {
    private static final char[] VALID_DIRECTIONS = {'L', 'U', 'R', 'D'};

    private final char direction;

    public MoveCommand(char direction) {
        this.direction = direction;

        for (char validDirection : VALID_DIRECTIONS) {
            if (validDirection == direction) break;
        }

        throw new IllegalArgumentException("Invalid direction: " + direction);
    }

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

        return MoveData.builder()
                .setDirection(direction)
                .build();
    }
}
