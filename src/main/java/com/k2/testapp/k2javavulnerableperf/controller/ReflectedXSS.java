package com.k2.testapp.k2javavulnerableperf.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/rxss")
public class ReflectedXSS {

    public static final String EMPTT = "";
    public static final String COUNT = "count";
    public static final String PAYLOAD = "payload";
    public static final String PAYLOAD_PARAM_NOT_FOUND = "payload param not found";

    public static String BASE_TEMPLATE = "<html><body><p>Hello %s</p></body></html>";
    
    @RequestMapping(value = "/{payload}", method = RequestMethod.GET)
    public String sendResponse(@PathVariable String payload) {
        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String sendResponseByQueryParam(@RequestParam String payload) {
        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String sendResponseByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTT;
        if(paramMap.containsKey(PAYLOAD)) {
            output = String.format(BASE_TEMPLATE, paramMap.get(PAYLOAD));
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PAYLOAD_PARAM_NOT_FOUND);
        }
        return output;
    }

    @RequestMapping(value = "/{payload}/response", method = RequestMethod.GET)
    public String sendResponse1(@PathVariable String payload) {

        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }
}
