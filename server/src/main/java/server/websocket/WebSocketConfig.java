package server.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UpdateHandler updateHandler;

    /**
     * Constructor using dependency injection. Spring boot automatically injects update handler
     * @param updateHandler configs how to handle updates
     */
    public WebSocketConfig(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    /**
     * Add a path to subscribe to for updates using web sockets (NOT HTTP, YES WS)
     * @param registry registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(updateHandler,"/update").setAllowedOrigins("*");

    }
}
