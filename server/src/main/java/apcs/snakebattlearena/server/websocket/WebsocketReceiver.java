package apcs.snakebattlearena.server.websocket;

import apcs.snakebattlearena.models.*;
import apcs.snakebattlearena.server.game.GameConfig;
import apcs.snakebattlearena.server.game.GameService;
import apcs.snakebattlearena.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
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
    @Autowired
    private WebsocketUserManager websocketUsers;

    /**
     * Handle new snakes connecting and make new entity for them.
     */
    @MessageMapping("/join")
    @SendToUser(value = "/topic/join", broadcast = false)
    private JoinResponseData join(JoinData joinData, Principal user) {
        // Check that the client major/minor version matches the server
        if (!Constants.VERSION.equalsIgnore(joinData.getClientVersion(), false, true)) {
            return JoinResponseData.Builder.builder()
                    .setError(JoinError.VERSION_MISMATCH)
                    .build();
        }

        // Add new player to the game
        Optional<JoinError> err = game.addPlayer(user.getName(), joinData.getSnake());

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
    private void leave(Principal user) {
        if (user == null) return;

        if (game.removePlayer(user.getName())) {
            websocketUsers.disconnectUser(user.getName());
        }
    }

    /**
     * Handle authenticated incoming move commands ({@link Direction})
     * based on the source websocket.
     */
    @MessageMapping("/move")
    private void move(MoveData data, Principal user) {
        if (user == null) return;

        game.addPlayerMoveToQueue(user.getName(), data);
    }
}
