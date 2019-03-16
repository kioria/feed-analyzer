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
 "Lacalut"
 ],
 "uid": "0"
 }
 ],
 "object": "toothpaste"
 }
 */
public class Notification {
    /*
    The object's type
     */
    private String object;
    /*
    An array containing an object describing the changes.
    Multiple changes from different objects that are of the same type may be batched together.
     */
    private List<Entry> entry;
}
