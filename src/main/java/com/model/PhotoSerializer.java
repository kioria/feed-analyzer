package com.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/*
 * { "field": "photos", "value": { "verb": "add", "object_id": "4444444444" } }
 */
public class PhotoSerializer extends JsonSerializer<Photo> {
    @Override
    public void serialize(
            Photo photo,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("field", photo.getField());
        jsonGenerator.writeObjectFieldStart("value");
        jsonGenerator.writeStringField("verb", photo.getVerb());
        jsonGenerator.writeStringField("object_id", photo.getObject_id());
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
    }
}
