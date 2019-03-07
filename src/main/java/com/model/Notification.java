package com.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
/**
 *
 {
 "entry": [
 {
 "time": 1551962658,
 "id": "0",
 "changed_fields": [
 "photos"
 ],
 "uid": "0"
 }
 ],
 "object": "user"
 }
 */
public class Notification {
    private String object;
    private List<Entry> entry;

}
