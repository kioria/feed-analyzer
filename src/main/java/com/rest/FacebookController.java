package com.rest;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FacebookController {
    @RequestMapping(value = "/facebook", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity validate(
            @RequestParam(value = "hub.mode", defaultValue = "subscribe") String hubMode,
            @RequestParam(value = "hub.challenge") int hubChallenge,
            @RequestParam(value = "hub.verify_token") String hubVerifyToken) {
        if ("subscribe".equalsIgnoreCase(hubMode) && "myToken".equalsIgnoreCase(hubVerifyToken)) {
            return ResponseEntity.ok(hubChallenge);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/facebook", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity processEventNotification(HttpServletRequest req, HttpServletResponse res) {
         Map<String, List<String>> inputParams = new HashMap<>();
        req.getParameterMap().forEach((k,v) -> inputParams.put(k, Arrays.asList(v)));
        System.out.println(inputParams);
        return ResponseEntity.ok(Collections.emptyList());
    }
}
