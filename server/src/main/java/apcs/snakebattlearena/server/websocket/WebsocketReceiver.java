package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.*;
import apcs.snakebattlearena.server.game.GameConfig;
import apcs.snakebattlearena.server.game.GameService;
import apcs.snakebattlearena.util.SemVer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

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

    @Autowired
    private WebsocketUserManager websocketUsers;

    @Autowired
    private SimpMessagingTemplate messaging;

    /**
     * Handle new snakes connecting and make new entity for them.
     */
    @MessageMapping("/join")
    @SendToUser(value = "/topic/join", broadcast = false)
    private JoinResponseData join(JoinData joinData, Principal user) {
        // TODO: don't hardcode server version
        final SemVer serverVersion = new SemVer(1, 0, 0);

        // Check that the client major version matches the server
        if (!serverVersion.equalsIgnore(joinData.getClientVersion(), false, false)) {
            return JoinResponseData.Builder.builder()
                    .setError(JoinError.VERSION_MISMATCH)
                    .build();
        }

        // Add new player to the game
        UUID id = UUID.fromString(user.getName());
        Optional<JoinError> err = game.addPlayer(id, joinData.getSnake());

        // Return the error
        if (err.isPresent()) {
            return JoinResponseData.Builder.builder()
                    .setError(err.get())
                    .build();
        }

        // Return game data
        return JoinResponseData.Builder.builder()
                .setBoard(BoardData.Builder.builder()
                        .setWidth(config.getBoardWidth())
                        .setHeight(config.getBoardHeight()).build())
                .build();
    }

    /**
     * Handle authenticated disconnect requests based on the source websocket.
     */
    @MessageMapping("/leave")
    private void leave(String snakeName, Principal user) {
        if (!user.getName().equals(snakeName)) {
            return;
        }

        UUID uuid = UUID.fromString(user.getName());

        if (game.removePlayer(uuid)) {
            websocketUsers.disconnectUser(uuid);
        }
    }

    /**
     * Handle authenticated incoming move commands ({@link Direction})
     * based on the source websocket.
     */
    @MessageMapping("/move")
    private void move(MoveData data, Principal user) {
        game.addPlayerMoveToQueue(user.getName(), data);
    }
}
