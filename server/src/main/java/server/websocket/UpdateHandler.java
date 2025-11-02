package server.websocket;

import Serializers.UpdateMessageDecoder;
import Serializers.UpdateMessageEncoder;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@ServerEndpoint(value = "/update", encoders = {UpdateMessageEncoder.class}, decoders = {UpdateMessageDecoder.class})
public class UpdateHandler implements WebSocketHandler {

    private final List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void afterConnectionEstablished
            (WebSocketSession session) {
        System.out.println("Connection " + session.getId() + " established");
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session,
                              WebSocketMessage<?> message) throws Exception {
        synchronized (sessions) { // Synchronize during iteration
            for (WebSocketSession wsSession : sessions) {
                if (wsSession.isOpen() && !wsSession.getId().equals(session.getId())) {
                    wsSession.sendMessage(message);
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session,
                                     Throwable e) {
        e.printStackTrace();
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus closeStatus) {
        System.out.println("connection" + session.getId() + "closed");
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
