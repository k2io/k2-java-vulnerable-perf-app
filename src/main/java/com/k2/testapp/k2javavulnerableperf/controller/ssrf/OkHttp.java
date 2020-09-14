package com.k2.testapp.k2javavulnerableperf.controller.ssrf;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

@RestController
@RequestMapping("/ssrf/okhttpclient")
@Tag(name = "OK HTTP Controller", description = "APIs performing connectivity via OK HTTP Client but have some intentional vulnerabilities.")
public class OkHttp {

    public static final String EMPTY = "";
    public static final String COUNT = "count";
    public static final String url = "url";
    public static final String URL_PARAM_NOT_FOUND = "url param not found";
    public static final String ERROR_WHILE_FETCHING_URL_S_S_S = "Error while fetching url : %s : %s : %s";

    private String connect(String url) {
        String response = EMPTY;
        try {
            Response response1 =  new OkHttpClient().newCall(new Request.Builder().get().url(url).build()).execute();
            response = String.valueOf(response1.code());
            response1.body().close();
        } catch (Exception e) {
            return String.format(ERROR_WHILE_FETCHING_URL_S_S_S, url, e.getMessage(), e.getCause());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Sends a request to a given URL in the `url` query string field and collects the response using OK HTTP Client.")
    public String connectQueryParam(
            @Parameter(name = "url", description = "The string URL for the connectivity", examples = {
                    @ExampleObject(summary = "Attack Case", value = "https://google.com", name = "Attack Payload")
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
    @Operation(summary = "Sends a request to a given URL in the `url` parameter and collects the response using OK HTTP Client.")
    public String connectByBody(
            @Parameter(name = "url", description = "The string URL for the connectivity<br><br>Attack Case : `https://google.com`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String url,
            @Parameter(name = "count", description = "Number of time this connection call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
                    Integer count
    ) {
        String output = EMPTY;

        if (count == null || count < 1 || count > 50) {
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
