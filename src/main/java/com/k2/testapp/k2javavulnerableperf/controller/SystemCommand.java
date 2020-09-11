package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/rce")
public class SystemCommand {

    public static final String EMPTT = "";
    public static final String ERROR_S_S = "Error : %s : %s";
    public static final String COUNT = "count";
    public static final String ARG = "arg";
    public static final String LS_LA = "ls -la ";
    public static final String ARG_PARAM_NOT_FOUND = "arg param not found";
    public static final String STDOUT_S_BR_STDERR_S = "STDOUT : %s <br>\r\n STDERR : %s";

    private String execute(String command) {
        command = LS_LA + command;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            String stdIn = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            String stdErr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            return String.format(STDOUT_S_BR_STDERR_S, stdIn, stdErr);
        } catch (Exception e) {
            return String.format(ERROR_S_S, e.getMessage(), e.getCause());
        }
    }

    @RequestMapping(value = "/{arg}", method = RequestMethod.GET)
    public String executeCommand(@PathVariable String arg, @RequestParam(defaultValue = "1") long count) {
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
    @ApiOperation(value = "Executes insecure `ls` command on the given `arg` parameter")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully executed `ls` command")

    })
    public String executeCommandByQueryParam(@ApiParam(name = "arg", value = "The argument which is supplied to `ls` command")
                                             @RequestParam String arg,
                                             @RequestParam(defaultValue = "1") long count) {
        String output = EMPTT;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            output = execute(arg);
        }
        return output;
    }


    @ApiOperation(value = "Executes insecure `ls` command on the given `arg` parameter")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully executed `ls` command")

    })
    @PostMapping(path = "/",
            consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public String executeCommandByBody(
                    String arg, int count) {
        String output = EMPTT;

        if (count < 1 || count > 50) {
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
