package Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import commons.Note;

import java.io.IOException;


public class NoteSerializer extends JsonSerializer<Note> {

    /**
     *
     * @param note               COMPLETE THIS
     * @param jsonGenerator      COMPLETE THIS
     * @param serializerProvider COMPLETE THIS
     * @throws IOException COMPLETE THIS
     */
    @Override
    public void serialize(Note note,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        //Write the id, title and content of the notes
        jsonGenerator.writeNumberField("id", note.id);
        jsonGenerator.writeStringField("title", note.title);
        jsonGenerator.writeStringField("content", note.content);
        jsonGenerator.writeArrayFieldStart("tags");
        jsonGenerator.writeEndArray();
        //Write the collection only if it hasn't been written already
        // to prevent infinite circular reference.
        if (!note.serialized) {
            note.serialized = true;
            if (note.collection != null) {
                //Uses collection serializer
                jsonGenerator.writeObjectField("collection", note.collection);
            }
            note.serialized = false;
        }
        // End object
        jsonGenerator.writeEndObject();
    }
}
