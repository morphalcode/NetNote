package Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import commons.Collection;
import commons.Note;

import java.io.IOException;


public class CollectionSerializer extends JsonSerializer<Collection> {

    /**
     * Custom Json serializer
     *
     * @param collection         collection
     * @param jsonGenerator      jsonGenerator
     * @param serializerProvider serializerProvider
     * @throws IOException IOException
     */
    @Override
    public void serialize(Collection collection,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {

        jsonGenerator.writeStartObject();

        //Write the name and id of the collection
        jsonGenerator.writeNumberField("id", collection.id);
        jsonGenerator.writeStringField("name", collection.name);
        jsonGenerator.writeStringField("server", collection.getServer());

        //Only if a collection hasn't been serialized, write its notes.
        // This prevents circular reference in the Json.
        //So, the first time a collection is serialized, all fields are written.
        // In the second time, only name and id.
        if (!collection.serialized) {
            collection.serialized = true;
            if (collection.notes != null) {
                //Write the notes field
                jsonGenerator.writeArrayFieldStart("notes");
                for (Note note : collection.notes) {
                    //Uses the notes serializer
                    jsonGenerator.writeObject(note);
                }
                jsonGenerator.writeEndArray();
            }
            collection.serialized = false;
        }
        jsonGenerator.writeEndObject();
    }
}
