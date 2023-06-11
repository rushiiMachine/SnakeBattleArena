package apcs.snakebattlearena.server.websocket;

import com.sun.security.auth.UserPrincipal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.lang.ref.WeakReference;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Configure a SockJS Websocket receiver on (/) for communication over
 * the STOMP sub-protocol between:
 * <ul>
 *     <li><b>Server <--> Client</b>: Server sends game data, client sends commands about itself</li>
 *     <li><b>Server --> Dashboard</b>: Server sends game data</li>
 * </ul>
 */
@Configuration
@EnableWebSocketMessageBroker
@SuppressWarnings("unused")
public class WebsocketController implements WebSocketMessageBrokerConfigurer {
    /**
     * Stores all active websocket connections by the associated random principal ID -> websocket connection.
     */
    public final Map<String, WeakReference<WebSocketSession>> sessions = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/") // Register WebSocket handler to (/)
                .setAllowedOriginPatterns("*") // Disable CORS
                .setHandshakeHandler(new DefaultHandshakeHandler() { // Handle new connections to add a unique identifier to them
                    @Override
                    protected Principal determineUser(@NotNull ServerHttpRequest request,
                                                      @NotNull WebSocketHandler wsHandler,
                                                      @NotNull Map<String, Object> attributes) {
                        return new UserPrincipal(UUID.randomUUID().toString());
                    }
                })
                .withSockJS(); // Use the SockJS protocol
    }

    // Configure STOMP message filters
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Set the valid outbound topic prefixes as /topic
        registry.enableSimpleBroker("/topic");
        // Set the valid incoming topic prefixes as /client
        registry.setApplicationDestinationPrefixes("/client");
    }

    // Store all active websocket connections
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(handler -> new WebSocketHandlerDecorator(handler) {
            @Override
            public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
                Principal user = session.getPrincipal();

                logger.info("New websocket connection initialized! (User: {})",
                        user != null ? user.getName() : "UNKNOWN");

                // Store the new session
                if (user != null) {
                    synchronized (sessions) {
                        sessions.put(user.getName(), new WeakReference<>(session));
                    }
                }

                super.afterConnectionEstablished(session);
            }

            // TODO: remove player from game after disconnect
            @Override
            public void afterConnectionClosed(@NotNull WebSocketSession session,
                                              @NotNull CloseStatus closeStatus) throws Exception {
                Principal user = session.getPrincipal();

                logger.info("Websocket session has disconnected with code {}. (User: {})",
                        closeStatus.getCode(),
                        user != null ? user.getName() : "UNKNOWN");

                // Remove the stored session
                if (user != null) {
                    synchronized (sessions) {
                        sessions.remove(user.getName());
                    }
                }

                super.afterConnectionClosed(session, closeStatus);
            }
        });
    }
}
