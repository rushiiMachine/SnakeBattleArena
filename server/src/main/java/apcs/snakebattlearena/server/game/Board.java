package apcs.snakebattlearena.server.game;

import apcs.snakebattlearena.Point;
import apcs.snakebattlearena.entities.Apple;
import apcs.snakebattlearena.entities.EntityModifier;
import apcs.snakebattlearena.utils.Probability;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;

public class Board {
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
    private final int boardWidth, boardHeight;

    /**
     * Board with all available squares. Squares are stored by <code>board[x][y]</code>.
     */
    private final BoardSquare[][] board;

    public Board(int width, int height) {
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
     * Finds an open spot on the board, gets a random reward value
     * based on predefined apple rewards values, then makes an apple
     * then adds it to the board.
     */
    public Apple generateNewApple() {
        Random rnd = new Random();
        int x = -1, y = -1;

        // Find a random empty square on the board
        while (x < 0 || y < 0 || board[x][y].occupyingCount() >= 1) {
            x = rnd.nextInt(boardWidth);
            y = rnd.nextInt(boardHeight);
        }


        // Store the new apple
        Apple apple = EntityModifier.newApple(new Point(x, y), APPLES_RANDOM.getRandomValue());
        board[x][y].addOccupier(apple);
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
