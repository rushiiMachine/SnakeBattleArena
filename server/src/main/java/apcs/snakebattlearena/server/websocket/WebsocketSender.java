package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.TickData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Send outbound messages on the websocket.
 */
@Controller
@SuppressWarnings("unused")
public class WebsocketSender {
    @Autowired
    private SimpMessagingTemplate messaging;

    /**
     * Send a game tick to all clients, with the new/removed entity data.
     */
    public void sendTick(TickData data) {
        messaging.convertAndSend("/topic/tick", data);
    }
}
