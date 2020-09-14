package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
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
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request served successfully.")
})
@Tag(name = "RXSS Controller", description = "Simple APIs reverting you with data you throw at them.")
public class ReflectedXSS {

    public static final String EMPTT = "";
    public static final String COUNT = "count";
    public static final String PAYLOAD = "payload";
    public static final String PAYLOAD_PARAM_NOT_FOUND = "payload param not found";
    public static final String UNABLE_TO_URL_DECODE_THE_INPUT_S = "Unable to URL decode the input : %s";

    public static String BASE_TEMPLATE = "<html><body><p>Hello %s</p></body></html>";

    @RequestMapping(value = "/{payload}", method = RequestMethod.GET)
    @Operation(summary = "Reverts a welcome message with the content of `payload` path parameter")
    public String sendResponse(
            @Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
                    @ExampleObject( summary  = "Normal Case", value = "USER", name = "Normal Payload")
            })
            @PathVariable String payload) {
        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }

    @GetMapping
    @Operation(summary = "Reverts a welcome message with the content of `payload` parameter")
    public String sendResponseByQueryParam(@Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload")
            })
            @RequestParam String payload
    ) {
        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Reverts a welcome message with the content of `payload` parameter")
    public String sendResponseByBody(
            @Parameter(name = "payload", description = "Data to construct the welcome message<br><br>Normal Case : `USER`", in= ParameterIn.DEFAULT, style = ParameterStyle.FORM
                    ,required = true)
                    String payload,
            @Parameter(name = "count", description = "Number of time this call is executed, Optional & defaults to `1`.", in= ParameterIn.DEFAULT, style = ParameterStyle.FORM)
            Integer count
    ) {
        String output = EMPTT;
        if (StringUtils.isNotBlank(payload)) {
            output = String.format(BASE_TEMPLATE, payload);
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PAYLOAD_PARAM_NOT_FOUND);
        }
        return output;
    }

    @RequestMapping(value = "/{payload}/response", method = RequestMethod.GET)
    @Operation(summary = "Reverts a welcome message with the content of `payload` path parameter")
    public String sendResponse1(
            @Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
                    @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload")
            })
            @PathVariable String payload) {

        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload);
        return output;
    }

    @RequestMapping(value = "/encoded", method = RequestMethod.GET)
    @Operation(summary = "Reverts a welcome message with the content of `payload` parameter in URL encoded format")
    public String sendResponseURLEncoded(@Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload")
            })
            @RequestParam String payload
    ) {

        String output = EMPTT;

        if (StringUtils.isNotBlank(payload)) {
            try {
                output = URLEncoder.encode(payload, StandardCharsets.UTF_8.name());
                output = String.format(BASE_TEMPLATE, output);

            } catch (UnsupportedEncodingException e) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, String.format(UNABLE_TO_URL_DECODE_THE_INPUT_S,  payload));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PAYLOAD_PARAM_NOT_FOUND);
        }
        return output;
    }
}
