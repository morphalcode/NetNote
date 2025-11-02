package Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.UpdateMessage;
import jakarta.websocket.EncodeException;
import jakarta.websocket.Encoder;

public class UpdateMessageEncoder implements Encoder.Text<UpdateMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String encode(UpdateMessage message) throws EncodeException {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            throw new EncodeException(message, "Error encoding UpdateMessage", e);
        }
    }
}
