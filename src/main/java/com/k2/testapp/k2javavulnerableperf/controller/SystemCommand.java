package com.k2.testapp.k2javavulnerableperf.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/rce")
public class SystemCommand {

    public static final String EMPTT = "";
    public static final String ERROR_S_S = "Error : %s : %s";
    public static final String COUNT = "count";
    public static final String COMMAND = "command";
    public static final String LS_LA = "ls -la ";
    public static final String COMMAND_PARAM_NOT_FOUND = "Command param not found";

    private String execute(String command){
        command = LS_LA + command;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            return IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return String.format(ERROR_S_S, e.getMessage(), e.getCause());
        }
    }

    @RequestMapping(value = "/{command}", method = RequestMethod.GET)
    public String executeCommand(@PathVariable String command, @RequestParam(defaultValue = "1") long count) {
        String output = EMPTT;
        if(count < 1){
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = execute(command);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String executeCommandByQueryParam(@RequestParam String command, @RequestParam(defaultValue = "1") long count) {
        String output = EMPTT;
        if(count < 1){
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = execute(command);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String executeCommandByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTT;
        long count = 1;
        if(paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if(count < 1){
            count = 1;
        }
        if(paramMap.containsKey(COMMAND)) {
            for (long i = 0; i < count; i++) {
                output = execute(paramMap.get(COMMAND));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, COMMAND_PARAM_NOT_FOUND);
        }
        return output;
    }
}
