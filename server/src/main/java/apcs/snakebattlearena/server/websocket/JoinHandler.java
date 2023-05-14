package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.BoardData;
import apcs.snakebattlearena.models.GameData;
import apcs.snakebattlearena.models.SnakeData;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@SuppressWarnings("unused")
public class JoinHandler {
    @MessageMapping("/join")
    @SendTo("/topic/join/data")
    public GameData join(SnakeData data) {
        return GameData.Builder.builder()
                .setBoard(BoardData.Builder.builder()
                        .setWidth(0)
                        .setHeight(0)
                        .build())
                .setSnakes(List.of(data))
                .build();
    }
}
