package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.MoveData;
import apcs.snakebattlearena.server.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * Handle incoming websocket STOMP messages.
 */
@Controller
@SuppressWarnings("unused")
public class WebsocketReceiver {
    @Autowired
    private GameService game;

    /**
     * Handle incoming move commands ({@link MoveData})
     */
    @MessageMapping("/move")
    public void move(MoveData data) {
        game.addPlayerMoveToQueue(data.getSnakeName(), data);
    }
}
