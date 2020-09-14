package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/rce")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully executed `ls` command")

})
@Tag(name = "System Command Controller", description = "APIs doing some risky un-validated exec calls on system.")
public class SystemCommand {

    public static final String EMPTT = "";
    public static final String ERROR_S_S = "Error : %s : %s";
    public static final String COUNT = "count";
    public static final String ARG = "arg";
    public static final String LS_LA = "ls -la ";
    public static final String ARG_PARAM_NOT_FOUND = "arg param not found";
    public static final String STDOUT_S_BR_STDERR_S = "STDOUT : %s \r\nSTDERR : %s";
    public static final String BIN_SH = "/bin/sh";
    public static final String C = "-c";

    private String execute(String command) {
        Process process = null;
        try {
            String[] cmd = {
                    BIN_SH,
                    C,
                    LS_LA + command
            };
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();
            String stdIn = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String stdErr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            return String.format(STDOUT_S_BR_STDERR_S, stdIn, stdErr);
        } catch (Exception e) {
            return String.format(ERROR_S_S, e.getMessage(), e.getCause());
        }
    }

    @RequestMapping(value = "/{arg}", method = RequestMethod.GET)
    @Operation(summary = "Executes `ls` command on the given `arg` path parameter")
    public String executeCommand(@Parameter(name = "arg", description = "The argument which is supplied to `ls` command", examples = {
            @ExampleObject(summary = "Normal Case", value = "test", name  = "Normal Payload")
    })
                                 @PathVariable String arg,
                                 @Parameter(name = "count", description = "Number of time this SystemCommand call is executed", hidden = true)
                                 @RequestParam(defaultValue = "1") int count) {
        String output = EMPTT;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            output = execute(arg);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Executes insecure `ls` command on the given `arg` parameter")
    public String executeCommandByQueryParam(@Parameter(name = "arg", description = "The argument which is supplied to `ls` command", examples = {
            @ExampleObject(summary = "Attack Case", value = "./ ; echo $(pwd)", name = "Attack Payload"),
            @ExampleObject(summary = "Normal Case", value = "./", name = "Normal Payload")

    })
                                             @RequestParam String arg,
                                             @Parameter(name = "count", description = "Number of time this SystemCommand call is executed", hidden = true)
                                             @RequestParam(defaultValue = "1") int count) {
        String output = EMPTT;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            output = execute(arg);
        }
        return output;
    }


    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    @Operation(summary = "Executes insecure `ls` command on the given `arg` parameter")
    public String executeCommandByBody(
            @Parameter(name = "arg", description = "The argument which is supplied to `ls` command<br><br>Attack Case  : `./ ; echo $(pwd)`<br><br>Normal Case : `./`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
            String arg,
            @Parameter(name = "count", description = "Number of time this SystemCommand call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
            Integer count) {
        String output = EMPTT;

        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if (StringUtils.isNotBlank(arg)) {
            for (long i = 0; i < count; i++) {
                output = execute(arg);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ARG_PARAM_NOT_FOUND);
        }
        return output;
    }
}
