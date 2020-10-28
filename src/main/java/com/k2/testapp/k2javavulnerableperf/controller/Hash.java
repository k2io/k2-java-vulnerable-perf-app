package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;


@RestController
@RequestMapping("/hash")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request served successfully.")
})
@Tag(name = "Hash Controller", description = "APIs doing some data hashing.")
public class Hash {
    private static final String ARG_PARAM_NOT_FOUND = "arg param not found";
    public static final String ERROR_S_S = "Error : %s : %s";

    @RequestMapping(method = RequestMethod.GET, path = "/md5")
    @Operation(summary = "Does a MD5 hashing (considered a weak hashing algorithm) on user defined argument given in `arg` parameter & reverts with response of the hashed data in Base64 encoding.")
    public String sendResponseByQueryParam(
            @Parameter(name = "arg", description = "User input to be used for hashing", examples = {
                    @ExampleObject(summary = "Normal Case", value = "hello", name = "Providing a simple string to be hashed.")
            })
            @RequestParam String arg,
            @Parameter(name = "algorithm", description = "Algorithm to be used for hashing. This field will accept all the natively provided hashing algorithm in Java Runtime", examples = {
                    @ExampleObject(summary = "Normal Case", value = "sha-256", name = "Providing a strong hashing algorithm."),
                    @ExampleObject(summary = "Weak Case", value = "md5", name = "Providing a weak hashing algorithm.")

            })
            @RequestParam String algorithm,
            @Parameter(name = "count", description = "Number of time this call is executed", hidden = true)
            @RequestParam(defaultValue = "1") Integer count) {
        String output = StringUtils.EMPTY;
        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        for (int i = 0; i < count; i++) {
            if (StringUtils.isNotBlank(arg)) {
                try {
                    MessageDigest md = MessageDigest.getInstance(algorithm);
                    output = Base64Utils.encodeToString(md.digest(arg.getBytes()));
                } catch (Exception e) {
                    return String.format(ERROR_S_S, e.getMessage(), e.getCause());
                }

            } else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, ARG_PARAM_NOT_FOUND);
            }
        }
        return output;
    }
}
