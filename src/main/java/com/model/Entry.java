package com.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {
 *  "time": 1551962658,
 *  "id": "0",
 *  "changed_fields": [
 *  "photos"
 *  ],
 *  "uid": "0"
 *  }
 */
@Data
@NoArgsConstructor
public class Entry {
    private String time;
    private String id;
    private List<String> changed_fields;
    private String uid;
}
