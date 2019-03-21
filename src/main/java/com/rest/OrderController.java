package com.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.Category;
import com.model.Notification;
import com.producer.FeedProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Models situation when clients do send data directly to our public API. Say, from mobile apps.
 * Thus handling failures gracefully using retries, message re-delivery, locking, and two-phase commits
 * is not possible.
 */
@RestController
public class OrderController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FeedProducer producer;
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity index() throws JsonProcessingException {
        return ResponseEntity.ok("Hello from order controller!");
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity processOrderNotification(@RequestBody Notification notification) throws JsonProcessingException {
        producer.sendOrder( objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notification));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/category", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity processCategoryNotification(@RequestBody Category category) throws JsonProcessingException {
        producer.sendCategory( category.getId(), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(category));
        return ResponseEntity.ok().build();
    }
}
