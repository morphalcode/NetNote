package Serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Collection;
import commons.Note;

import java.io.IOException;
import java.util.ArrayList;

public class NoteDeserializer extends JsonDeserializer<Note> {

    /**
     * COMPLETE THIS
     *
     * @param jsonParser             COMPLETE THIS
     * @param deserializationContext COMPLETE THIS
     * @return COMPLETE THIS
     * @throws IOException             COMPLETE THIS
     * @throws JsonProcessingException COMPLETE THIS
     */
    @Override
    public Note deserialize(JsonParser jsonParser,
                            DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode rootNode = mapper.readTree(jsonParser);

        // Deserialize the regular fields of Note
        Note note = new Note();
        note.id = rootNode.get("id").asLong();
        note.title = (rootNode.get("title").asText());
        note.content = (rootNode.get("content").asText());
        note.tags = new ArrayList<String>();

        if (rootNode.has("collection")) {
            Collection collection = mapper
                    .treeToValue(rootNode.get("collection"), Collection.class);
            note.collection = (collection);
            Note cur = note.collection.notes.stream()
                    .filter(x -> x.id == rootNode.get("id").asLong())
                    .findFirst()
                    .orElse(null);
            if (cur != null) {
                note.collection.notes.set(note.collection.notes.indexOf(cur), note);
            }
        }
        return note;
    }
}
