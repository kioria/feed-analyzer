package com.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * { "field": "photos", "value": { "verb": "add", "object_id": "4444444444" } }
 */
public class PhotoDeserializer extends JsonDeserializer<Photo> {
    @Override
    public Photo deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException {
        ObjectCodec objectCodec = jsonParser.getCodec();
        JsonNode jsonNode = objectCodec.readTree(jsonParser);

        Photo photo = new Photo();
        photo.setField(jsonNode.get("field").asText());
        photo.setVerb(jsonNode.get("value").get("verb").asText());
        photo.setObject_id(jsonNode.get("value").get("object_id").asText());
        return photo;
    }
}
