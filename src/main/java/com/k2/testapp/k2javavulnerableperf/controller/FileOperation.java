package com.k2.testapp.k2javavulnerableperf.controller;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileOperation {

    public static final String EMPTY = "";
    public static final String ERROR_WHILE_READING_FILE_S_S_S = "Error while reading file %s : %s : %s";
    public static final String COUNT = "count";
    public static final String PATH = "path";
    public static final String ERROR_WHILE_WRITING_FILE_S_S_S = "Error while writing file %s : %s : %s";
    public static final String DATA = "data";
    public static final String PATH_PARAM_NOT_FOUND = "'path' param not found";

    private String readFileData(String path){
        String data = EMPTY;
        try(FileInputStream stream = new FileInputStream(path)){
            data = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (Exception e){
            data = String.format(ERROR_WHILE_READING_FILE_S_S_S, path, e.getMessage(), e.getCause());
        }
        return data;
    }

    private String writeFileData(String path, String content){
        String data = EMPTY;
        try(FileOutputStream stream = new FileOutputStream(path)){
            IOUtils.write(content, stream, StandardCharsets.UTF_8);
            data = readFileData(path);
        } catch (Exception e){
            data = String.format(ERROR_WHILE_WRITING_FILE_S_S_S, path, e.getMessage(), e.getCause());
        }
        return data;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String readFilePathByQueryParam(@RequestParam String path, @RequestParam(defaultValue = "1") long count) {
        String output = EMPTY;
        if(count < 1){
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = readFileData(path);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String readFilePathByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTY;
        long count = 1;
        if(paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if(count < 1){
            count = 1;
        }
        if(paramMap.containsKey(PATH)) {
            for (long i = 0; i < count; i++) {
                output = readFileData(paramMap.get(PATH));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PATH_PARAM_NOT_FOUND);
        }
        return output;
    }

    @RequestMapping(value = "/write", method = RequestMethod.GET)
    public String writeFilePathByQueryParam(@RequestParam String path, @RequestParam(defaultValue = "1") long count, @RequestParam String data) {
        String output = EMPTY;
        if (count < 1) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            output = writeFileData(path, data);
        }
        return output;
    }

    @RequestMapping(value = "/write", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String writeFilePathByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTY;
        long count = 1;
        if(paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if(count < 1){
            count = 1;
        }
        if(paramMap.containsKey(PATH)) {
            for (long i = 0; i < count; i++) {
                output = writeFileData(paramMap.get(PATH), paramMap.get(DATA));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PATH_PARAM_NOT_FOUND);
        }
        return output;
    }
}
