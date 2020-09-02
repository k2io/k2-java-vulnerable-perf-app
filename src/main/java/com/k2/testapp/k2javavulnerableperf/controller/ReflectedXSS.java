package com.k2.testapp.k2javavulnerableperf.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/rxss")
public class ReflectedXSS {

    public static final String EMPTT = "";
    public static final String COUNT = "count";
    public static final String PAYLOAD = "payload";
    public static final String PAYLOAD_PARAM_NOT_FOUND = "payload param not found";
    public static final String UNABLE_TO_URL_DECODE_THE_INPUT_S = "Unable to URL decode the input : %s";

    public static String BASE_TEMPLATE = "<html><body><p>Hello %s</p></body></html>";

    @RequestMapping(value = "/{payload}", method = RequestMethod.GET)
    public String sendResponse(@PathVariable String payload) {
        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }

    @GetMapping
    public String sendResponseByQueryParam(@RequestParam Map<String, String> queryParams) {
        String output = EMPTT;
        if (queryParams.containsKey(PAYLOAD)) {
            output = String.format(BASE_TEMPLATE, queryParams.get(PAYLOAD));
        } else {
            output = String.format(BASE_TEMPLATE, EMPTT);

        }
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String sendResponseByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTT;
        if (paramMap.containsKey(PAYLOAD)) {
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

    @RequestMapping(value = "/encoded", method = RequestMethod.GET)
    public String sendResponseURLEncoded(@RequestParam Map<String, String> queryParams) {

        String output = EMPTT;

        if (queryParams.containsKey(PAYLOAD)) {
            try {
                output = URLEncoder.encode(queryParams.get(PAYLOAD), StandardCharsets.UTF_8.name());
                output = String.format(BASE_TEMPLATE, output);

            } catch (UnsupportedEncodingException e) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, String.format(UNABLE_TO_URL_DECODE_THE_INPUT_S,  queryParams.get(PAYLOAD)));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PAYLOAD_PARAM_NOT_FOUND);
        }
        return output;
    }
}
