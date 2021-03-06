package com.k2.testapp.k2javavulnerableperf.controller.ssrf;

import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/ssrf/akka")
@Tag(name = "SSRF Controller", description = "APIs performing connectivity via HTTP Clients but have some intentional vulnerabilities.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Outbound HTTP request operation successful")
})
public class AkkaHttp {

    public static final String EMPTY = "";
    public static final String COUNT = "count";
    public static final String url = "url";
    public static final String URL_PARAM_NOT_FOUND = "url param not found";
    public static final String ERROR_WHILE_FETCHING_URL_S_S_S = "Error while fetching url : %s : %s : %s";
    private static  final ActorSystem system = ActorSystem.create();


    private String connect(String url) {
        String response = EMPTY;
        try {
            ActorMaterializer materializer = ActorMaterializer.create(system);

            HttpRequest request = HttpRequest.create(url);
            CompletionStage<HttpResponse> responseFuture = Http.get(system).singleRequest(request);

            HttpResponse httpResponse = responseFuture.toCompletableFuture().get();
            response = String.valueOf(httpResponse.status().intValue());
            httpResponse.discardEntityBytes(materializer);
        } catch (Exception e) {
            return String.format(ERROR_WHILE_FETCHING_URL_S_S_S, url, e.getMessage(), e.getCause());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Sends a request to a given URL in the `url` query string field and collects the response using Akka HTTP Client.")
    public String connectQueryParam(
            @Parameter(name = "url", description = "The string URL for the connectivity", examples = {
                    @ExampleObject(summary = "Attack Case", value = "https://google.com", name = "Attack Payload"),
                    @ExampleObject(summary = "Normal Case", value = "http://localhost:8080/rxss/hello", name = "Normal Payload")
            })
            @RequestParam String url,
            @Parameter(name = "count", description = "Number of time this connection call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        String output = EMPTY;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            output = connect(url);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Sends a request to a given URL in the `url` parameter and collects the response using Akka HTTP Client.")
    public String connectByBody(
            @Parameter(name = "url", description = "The string URL for the connectivity<br><br>Attack Case : `https://google.com` <br><br>Normal Case : `http://localhost:8080/rxss/hello`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
            String url,
            @Parameter(name = "count", description = "Number of time this connection call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
            Integer count) {
        String output = EMPTY;
        if (count == null ||count < 1 || count > 50) {
            count = 1;
        }
        if (StringUtils.isNotBlank(url)) {
            for (long i = 0; i < count; i++) {
                output = connect(url);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, URL_PARAM_NOT_FOUND);
        }
        return output;
    }
}
