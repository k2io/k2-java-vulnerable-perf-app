package com.k2.testapp.k2javavulnerableperf.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/rxss")
public class ReflectedXSS {

    public static final String EMPTT = "";
    public static final String COUNT = "count";
    public static final String PAYLOAD = "payload";

    public static String BASE_TEMPLATE = "<html><body><p>Hello %s</p></body></html>";
    
    @RequestMapping(value = "/{payload}", method = RequestMethod.GET)
    public String sendResponse(@PathVariable String payload, @RequestParam(defaultValue = "1") long count) {
        String output = EMPTT;
        if(count < 1){
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = String.format(BASE_TEMPLATE, payload);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String sendResponseByQueryParam(@RequestParam String payload, @RequestParam(defaultValue = "1") long count) {
        String output = EMPTT;
        if(count < 1){
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = String.format(BASE_TEMPLATE, payload);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String sendResponseByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTT;
        long count = Long.parseLong(paramMap.get(COUNT));
        if(count < 1){
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = String.format(BASE_TEMPLATE, paramMap.get(PAYLOAD));
        }
        return output;
    }
}
