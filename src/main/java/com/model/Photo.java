package com.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonDeserialize(using = PhotoDeserializer.class)
@JsonSerialize(using = PhotoSerializer.class)
public class Photo {

    private String field;
    private String verb;
    private String object_id;

}
