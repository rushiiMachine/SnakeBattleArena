package apcs.snakebattlearena.server.game;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * Injected config loaded and validated by Spring
 * from any <code>application.properties</code> file present.
 */
@Validated
@Configuration
@ConfigurationProperties(prefix = "game")
public class GameConfig {
    @Min(0)
    public int appleCount;
    @Min(1)
    private short boardWidth;
    @Min(1)
    private short boardHeight;

    // ------------------ Getters and required auto-setters ------------------

    public short getBoardWidth() {
        return boardWidth;
    }

    public void setBoardWidth(short boardWidth) {
        this.boardWidth = boardWidth;
    }

    public short getBoardHeight() {
        return boardHeight;
    }

    public void setBoardHeight(short boardHeight) {
        this.boardHeight = boardHeight;
    }

    public int getAppleCount() {
        return appleCount;
    }

    public void setAppleCount(int appleCount) {
        this.appleCount = appleCount;
    }
}
