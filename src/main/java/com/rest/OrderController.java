package com.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FeedProducer producer;
    private static List<Notification> notifications = new ArrayList<>();
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity index() throws JsonProcessingException {
        String jsonOutput = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notifications);
        return ResponseEntity.ok(jsonOutput);
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity processOrderNotification(@RequestBody Notification notification) throws JsonProcessingException {
        System.out.println("/order endpoint called with request " + notification);
        notifications.add(notification);
        producer.sendMessage( objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(notification));
        return ResponseEntity.ok(notifications);
    }
}