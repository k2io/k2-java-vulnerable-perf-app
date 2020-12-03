package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public static final Set<String> expectedHeaders = new HashSet<>(Arrays.asList("accept","accept-encoding",
            "accept-language", "connection", "cookie", "k2-fuzz-request-id", "user-agent", "host", "origin", "referer"
    ));

    public static final String S_BR_FOLLOWING_IS_THE_LIST_OF_UNEXPECTED_HEADERS_ARE_S = "%s<br>Following are the unexpected headers : %s";

    public static String BASE_TEMPLATE = "<html><body><p>Hello %s</p></body></html>";

    @RequestMapping(value = "/multiParam", method = RequestMethod.GET)
    @Operation(summary = "Reverts a welcome message with the content of `payload` parameter with optional payloadExtension param")
    public String sendResponseByQueryParamMulti(@Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload"),
            @ExampleObject(summary = "Attack Case", value = "USER <script>alert('attack')</script>", name = "Attack Payload")

    }) @RequestParam String payload,

        @Parameter(name = "payloadExtension", description = "Extra data to construct the welcome message", examples = {
                @ExampleObject(summary = "Normal Case", value = "EXT", name = "Normal Payload"),
                @ExampleObject(summary = "Attack Case", value = "USER <script>alert('attack from ext')</script>", name = "Attack Payload")
        }) @RequestParam(defaultValue = "EXT") String payloadExtension
    ) {
        String output = EMPTT;
        output = String.format(BASE_TEMPLATE, payload + StringUtils.SPACE + payloadExtension);
        return output;
    }

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
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload"),
            @ExampleObject(summary = "Attack Case", value = "USER <script>alert('attack')</script>", name = "Attack Payload")

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
            @Parameter(name = "payload", description = "Data to construct the welcome message<br><br>Normal Case : `USER`<br><br>Attack Case : `USER <script>alert('attack')</script>`", in= ParameterIn.DEFAULT, style = ParameterStyle.FORM
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
    @Operation(summary = "Reverts a welcome message with the content of `payload` parameter in HTML encoded format")
    public String sendResponseHTMLEncoded(@Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload")
            })
            @RequestParam String payload
    ) {

        String output = EMPTT;

        if (StringUtils.isNotBlank(payload)) {
            try {
                output = HtmlUtils.htmlEscape(payload, StandardCharsets.UTF_8.name());
                output = String.format(BASE_TEMPLATE, output);

            } catch (Exception e) {
                throw new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, String.format(UNABLE_TO_URL_DECODE_THE_INPUT_S,  payload));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PAYLOAD_PARAM_NOT_FOUND);
        }
        return output;
    }

    @RequestMapping(value = "/header", method = RequestMethod.GET)
    @Operation(summary = "Reverts a welcome message with the content of `payload` header parameter ")
    public String sendResponseHeader(@Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload")
    })
         @RequestHeader String payload
    ) {

        String output = String.format(BASE_TEMPLATE, payload);

        return output;
    }

    @Hidden
    @RequestMapping(value = "/checkunwantedheader", method = RequestMethod.GET)
    @Operation(summary = "Reverts a welcome message with the content of `payload` header parameter along with a list of unwanted header fields from request along with ")
    public String sendResponseUnwantedHeader(@Parameter(name = "payload", description = "Data to construct the welcome message", examples = {
            @ExampleObject(summary = "Normal Case", value = "USER", name = "Normal Payload")
    })
             @RequestHeader String payload,
             @RequestHeader Map<String, String> headers
    ) {
        Map<String, String> unexpectedHeaders = new HashMap<>();
        if(headers != null && !headers.isEmpty()){
            for(String key : headers.keySet()){
                if(!expectedHeaders.contains(key) && !StringUtils.equals(key, PAYLOAD)){
                    unexpectedHeaders.put(key, headers.get(key));
                }
            }
        }

        String consolidatedPayload = String.format(S_BR_FOLLOWING_IS_THE_LIST_OF_UNEXPECTED_HEADERS_ARE_S, payload, unexpectedHeaders);
        String output = String.format(BASE_TEMPLATE, consolidatedPayload);

        return output;
    }
}
