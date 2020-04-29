package com.k2.testapp.k2javavulnerableperf.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/deserialise")
public class Deserialisation {

    public static final String EMPTY = "";
    public static final String COUNT = "count";
    public static final String PAYLOAD = "payload";
    public static final String ERROR_WHILE_DECODING_PAYLOAD_S_S_S = "Error while decoding payload : %s :: %s : %s";
    public static final String PAYLOAD_PARAM_NOT_FOUND = "payload param not found";
    public static final String DESERIALISED_OBJECT_IS_NULL = "Deserialised object is null";

    private String parseExpression(String encodedPayload) {
        String response = EMPTY;
        try {
            byte[] decodedPayload = Base64.getDecoder().decode(encodedPayload);
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedPayload);
                 ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream)) {
                Object deserialisedObject = inputStream.readObject();
                if(deserialisedObject != null) {
                    response = String.format("Deserialised object is : %s :: %s", deserialisedObject.getClass(), deserialisedObject.toString());
                } else {
                    response = DESERIALISED_OBJECT_IS_NULL;
                }
            }
        } catch (Exception e) {
            response = String.format(ERROR_WHILE_DECODING_PAYLOAD_S_S_S, encodedPayload, e.getMessage(), e.getCause());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String sendResponseByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTY;
        long count = 1;
        if (paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if (count < 1) {
            count = 1;
        }
        if (paramMap.containsKey(PAYLOAD)) {
            for (long i = 0; i < count; i++) {
                output = parseExpression(paramMap.get(PAYLOAD));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PAYLOAD_PARAM_NOT_FOUND);
        }
        return output;
    }
}
