package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Apple;
import apcs.snakebattlearena.entities.EntityModifier;
import apcs.snakebattlearena.utils.Probability;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class Board {
    private final Random rnd = new Random();

    /**
     * Random weighted apple reward values
     */
    private final Probability<Integer> APPLES_RANDOM = new Probability<Integer>() {{
        addValue(4, 0.10);
        addValue(3, 0.15);
        addValue(2, 0.25);
        addValue(1, 0.50);
    }};

    /**
     * Board width/height measured in square count.
     */
    private final short boardWidth, boardHeight;

    /**
     * Board with all available squares. Squares are stored by <code>board[x][y]</code>.
     */
    private final BoardSquare[][] board;

    public Board(short width, short height) {
        this.boardWidth = width;
        this.boardHeight = height;

        // Initialize a blank board filled with squares
        this.board = new BoardSquare[boardWidth][boardHeight];
        Arrays.parallelSetAll(this.board, i -> {
            BoardSquare[] column = new BoardSquare[boardHeight];
            Arrays.parallelSetAll(column, i2 -> new BoardSquare());
            return column;
        });
    }

    /**
     * Gets all the squares for this board.
     * TODO: remove this once all board logic moved here
     */
    public BoardSquare[][] getBoard() {
        return board;
    }

    /**
     * Gets this board's width in squares.
     */
    public int getBoardWidth() {
        return boardWidth;
    }

    /**
     * Gets this board's height in squares.
     */
    public int getBoardHeight() {
        return boardHeight;
    }

    /**
     * Gets the board square at that point if it exists.
     * @param point Nullable point of the square.
     * @return A square if found, otherwise null.
     */
    @Nullable
    public BoardSquare getSquare(@Nullable Point point) {
        if (point == null) return null;
        if (!point.isOnBoard(boardWidth, boardHeight)) return null;

        return board[point.getX()][point.getY()];
    }

    /**
     * Check if the target point is located within the board bounds.
     * @param point Nullable point
     */
    public boolean isPointOnBoard(@Nullable Point point) {
        if (point == null) return false;
        return point.isOnBoard(boardWidth, boardHeight);
    }

    /**
     * Find an open square on this board.
     * @return A point to that square or an exception if its full.
     * TODO: figure out what to do when full?
     */
    @NotNull
    public Point getRandomPoint() {
        int x = -1, y = -1;
        int attempts = 0;

        while (x < 0 || y < 0 || board[x][y].occupyingCount() >= 1) {
            if (++attempts > boardWidth * boardHeight) {
                throw new IllegalStateException("Board is full!");
            }

            x = rnd.nextInt(boardWidth);
            y = rnd.nextInt(boardHeight);
        }

        return new Point(x, y);
    }

    /**
     * Finds an open spot on the board, gets a random reward value
     * based on predefined apple rewards values, then makes an apple
     * then adds it to the board.
     */
    public Apple generateNewApple() {
        Point point = getRandomPoint();

        // Store the new apple
        Apple apple = EntityModifier.newApple(point, APPLES_RANDOM.getRandomValue());
        Objects.requireNonNull(getSquare(point)).addOccupier(apple);
        return apple;
    }

    /**
     * Internal debugging utility for printing the board to the console in a pretty format.
     * Since the board is stored as (x,y) then we have to rotate it 90° and flip vertically.
     */
    public void printBoardToConsole() {
        StringBuffer buf = new StringBuffer((boardHeight + 1) * boardWidth);

        // Prefill with newlines for most of them to be replaced below
        for (int i = 0; i < buf.capacity(); i++) {
            buf.append('\n');
        }

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                int index = y * (boardWidth + 1) + x;
                char display = board[x][y].occupyingCount() > 0 ? 'X' : '.';
                buf.setCharAt(index, display);
            }
        }

        System.out.println(Strings.repeat("-", boardWidth));
        System.out.print(buf);
        System.out.println(Strings.repeat("-", boardWidth));
    }
}
