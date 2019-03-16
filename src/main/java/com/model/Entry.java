package com.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * {
 *  "time": 1551962658,
 *  "id": "0",
 *  "changed_fields": [
 *  "toothpaste"
 *  ],
 *  "uid": "0"
 *  }
 */
@Data
@NoArgsConstructor
public class Entry {
    //A UNIX timestamp indicating when the Event Notification was sent (not when the change that triggered the notification occurred).
    private String time;
    //The object's ID
    private String id;
    //An array of strings indicating the names of the fields that have been changed
    private List<String> changed_fields;
    private String uid;
}
