package com.k2.testapp.k2javavulnerableperf.controller.ssrf;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/ssrf/httpconnection")
public class HttpConnection {

    public static final String EMPTY = "";
    public static final String COUNT = "count";
    public static final String url = "url";
    public static final String URL_PARAM_NOT_FOUND = "url param not found";
    public static final String ERROR_WHILE_FETCHING_URL_S_S_S = "Error while fetching url : %s : %s : %s";

    private String connect(String url) {
        String response = EMPTY;
        try {
            URL url1 = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)url1.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            response = String.valueOf(connection.getResponseCode());
            connection.disconnect();
        } catch (Exception e) {
            return String.format(ERROR_WHILE_FETCHING_URL_S_S_S, url, e.getMessage(), e.getCause());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String connectQueryParam(@RequestParam String url, @RequestParam(defaultValue = "1") long count) {
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
    public String connectByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTY;
        long count = 1;
        if (paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if (count < 1 || count > 50) {
            count = 1;
        }
        if (paramMap.containsKey(url)) {
            for (long i = 0; i < count; i++) {
                output = connect(paramMap.get(url));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, URL_PARAM_NOT_FOUND);
        }
        return output;
    }
}
