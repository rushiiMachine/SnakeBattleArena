package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.MoveData;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

/**
 * Handle incoming websocket messages with the prefix /app/commands
 */
@Controller
@SuppressWarnings("unused")
public class CommandHandler {
    /**
     * Handle incoming move commands ({@link MoveData})
     */
    @MessageMapping("/commands/move")
    public void move(MoveData data) {
        System.out.println(data.toString());
    }
}
