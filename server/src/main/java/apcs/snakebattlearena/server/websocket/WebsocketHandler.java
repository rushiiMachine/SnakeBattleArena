package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.MoveData;
import apcs.snakebattlearena.models.TickData;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Handle incoming websocket STOMP messages.
 */
@Controller
@SuppressWarnings("unused")
public class WebsocketHandler {
    /**
     * Handle incoming move commands ({@link MoveData})
     */
    @MessageMapping("/commands/move")
    private void move(@Payload MoveData data) {
        System.out.println(data.toString());
    }


    /**
     * Send a game tick to all clients, with the new/removed entity data.
     */
    @SendTo("/topic/tick")
    public TickData sendTick(TickData data) {
        return data;
    }
}
