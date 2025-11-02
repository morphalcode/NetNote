package Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.UpdateMessage;
import jakarta.websocket.Decoder;

public class UpdateMessageDecoder implements Decoder.Text<UpdateMessage> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public UpdateMessage decode(String string) {
        try {
            return objectMapper.readValue(string, UpdateMessage.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean willDecode(String string) {
        return string != null && !string.isEmpty();
    }
}