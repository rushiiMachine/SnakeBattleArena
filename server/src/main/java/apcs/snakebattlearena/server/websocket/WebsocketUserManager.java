package apcs.snakebattlearena.server.websocket;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Manage websocket connections based on their associated user ID.
 */
@Configuration
public class WebsocketUserManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WebsocketController controller;

    /**
     * Disconnects a user's websocket connection by their unique ID.
     * @return Whether the user had an active connection that was removed.
     */
    public boolean disconnectUser(@NotNull String id) {
        WebSocketSession session;

        // Get the WS session
        synchronized (controller.sessions) {
            WeakReference<WebSocketSession> sessionRef = controller.sessions.remove(id);

            if (sessionRef == null || (session = sessionRef.get()) == null) {
                return false;
            }
        }

        // Close the connection
        try {
            session.close();
            return true;
        } catch (IOException e) {
            logger.error("Failed to close websocket connection!", e);
        }

        return false;
    }
}
