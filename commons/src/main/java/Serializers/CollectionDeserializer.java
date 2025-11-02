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
import java.util.List;

public class CollectionDeserializer extends JsonDeserializer<Collection> {

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
    public Collection deserialize(JsonParser jsonParser,
                                  DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {

        ObjectMapper mapper = (ObjectMapper) jsonParser.getCodec();
        JsonNode rootNode = mapper.readTree(jsonParser);

        // Deserialize the regular fields of collection
        Collection collection = new Collection();
        collection.id = (rootNode.get("id").asLong());
        collection.name = (rootNode.get("name").asText());
        collection.setServer((rootNode.get("server").asText()));

        // Deserialize the derived fields with circular reference
        List<Note> notes = new ArrayList<>();
        if (rootNode.has("notes")) {
            for (JsonNode noteNode : rootNode.get("notes")) {
                Note note = mapper.treeToValue(noteNode, Note.class);
                note.collection = (collection);
                notes.add(note);
            }
        }
        collection.notes = (notes);

        return collection;
    }
}
