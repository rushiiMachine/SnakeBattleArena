package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.BoardData;
import apcs.snakebattlearena.models.MoveData;
import apcs.snakebattlearena.models.entities.SnakeMetadata;
import apcs.snakebattlearena.server.game.GameConfig;
import apcs.snakebattlearena.server.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Optional;

/**
 * Handle incoming websocket STOMP messages.
 */
@Controller
@SuppressWarnings("unused")
public class WebsocketReceiver {
    @Autowired
    private GameService game;

    @Autowired
    private GameConfig config;

    /**
     * Handle new snakes connecting
     * TODO: only allow one per WS connection
     */
    @MessageMapping("/join")
    @SendTo("/topic/join/data")
    private Optional<BoardData> join(SnakeMetadata snake) {
        if (game.addPlayer(snake)) {
            BoardData data = BoardData.Builder.builder()
                    .setWidth(config.getBoardWidth())
                    .setHeight(config.getBoardHeight())
                    .build();
            return Optional.of(data);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Handle incoming move commands ({@link MoveData})
     */
    @MessageMapping("/move")
    private void move(MoveData data) {
        game.addPlayerMoveToQueue(data.getSnakeName(), data);
    }
}
