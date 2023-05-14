package apcs.snakebattlearena.server.websocket;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

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
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/") // Register WebSocket handler to (/)
                .setAllowedOriginPatterns("*") // Disable CORS
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

    @EventListener(SessionConnectEvent.class)
    public void onApplicationEvent(@NotNull SessionConnectEvent event) {
        System.out.println("New client has conntected.");
    }

    @EventListener(SessionDisconnectEvent.class)
    public void onApplicationEvent(SessionDisconnectEvent event) {
        System.out.println(event.getSessionId() + " has disconnected.");
    }
}
