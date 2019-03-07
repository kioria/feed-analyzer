package com.rest;

import com.model.Photo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class FacebookController {
    private static List<Photo> photos = new ArrayList<>();
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity index() {
        return ResponseEntity.ok(photos);
    }
    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity validate(
            @RequestParam(value = "hub.mode", defaultValue = "subscribe") String hubMode,
            @RequestParam(value = "hub.challenge") int hubChallenge,
            @RequestParam(value = "hub.verify_token") String hubVerifyToken) {
        if ("subscribe".equalsIgnoreCase(hubMode) && "dana".equalsIgnoreCase(hubVerifyToken)) {
            return ResponseEntity.ok(hubChallenge);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/facebook", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity processEventNotification(@RequestBody Photo photo) {
        System.out.println("/facebook endpoint called with request " + photo);
        photos.add(photo);
        ResponseEntity<List<Photo>> ok = ResponseEntity.ok(photos);
        System.out.println("/facebook endpoint returning " + ok);
        return ok;
    }
}
