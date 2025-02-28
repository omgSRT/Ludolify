package com.omgsrt.Ludolify.shared.jacksonSerializer;

import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    @Override
    public ObjectId deserialize(com.fasterxml.jackson.core.JsonParser jsonParser,
                                com.fasterxml.jackson.databind.DeserializationContext deserializationContext)
            throws IOException {
        return new ObjectId(jsonParser.getText());
    }
}
