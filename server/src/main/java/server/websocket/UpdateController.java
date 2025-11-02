package server.websocket;

import commons.UpdateMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class UpdateController {
    @MessageMapping("send")
    @SendTo("updates")
    public UpdateMessage send(UpdateMessage message) {
        return message;
    }
}
