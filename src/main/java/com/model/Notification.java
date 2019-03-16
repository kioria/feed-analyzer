package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

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
