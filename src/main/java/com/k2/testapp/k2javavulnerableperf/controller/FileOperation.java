package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/file")
@Tag(name = "File Controller", description = "APIs doing File operations via Java built-in APIs but have some intentional vulnerabilities.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File Operation successful")
})
public class FileOperation {

    public static final String EMPTY = "";
    public static final String ERROR_WHILE_READING_FILE_S_S_S = "Error while reading file %s : %s : %s";
    public static final String COUNT = "count";
    public static final String PATH = "path";
    public static final String ERROR_WHILE_WRITING_FILE_S_S_S = "Error while writing file %s : %s : %s";
    public static final String DATA = "data";
    public static final String PATH_PARAM_NOT_FOUND = "'path' param not found";
    public static final String FILE_NOT_FOUND_S = "File not found : %s";
    public static final String FILE_ALREADY_EXISTS_S = "File already exists : %s";

    private String readFileData(String path){
        String data = EMPTY;
        try(FileInputStream stream = new FileInputStream(path)){
            data = IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (Exception e){
            data = String.format(ERROR_WHILE_READING_FILE_S_S_S, path, e.getMessage(), e.getCause());
        }
        return data;
    }

    private String writeFileData(String path, String content) {
        return writeFileData(path, content, true);
    }

    private String writeFileData(String path, String content, boolean readAfterWrite){
        String data = EMPTY;
        try(FileOutputStream stream = new FileOutputStream(path)){
            IOUtils.write(content, stream, StandardCharsets.UTF_8);
            if(readAfterWrite) {
                data = readFileData(path);
            }
        } catch (Exception e){
            data = String.format(ERROR_WHILE_WRITING_FILE_S_S_S, path, e.getMessage(), e.getCause());
        }
        return data;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Reads the file by file path(String) given in the query string field name `path`")
    public String readFilePathByQueryParam(
            @Parameter(name = "path", description = "The file path for read", examples = {
                    @ExampleObject(summary = "Normal Case", value = "testfile", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "/etc/passwd", name = "Accessing file outside application context via absolute path.")
            })
            @RequestParam String path,
            @Parameter(name = "count", description = "Number of time this File Read call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        String output = EMPTY;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = readFileData(path);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/checkread")
    @Operation(summary = "Check & reads the file by file path(String) given in the query string field name `path` only if the file exists")
    public String checkReadFilePathByQueryParam(
            @Parameter(name = "path", description = "The file path for read", examples = {
                    @ExampleObject(summary = "Normal Case", value = "testfile", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "/etc/passwd", name = "Accessing file outside application context via absolute path.")
            })
            @RequestParam String path,
            @Parameter(name = "count", description = "Number of time this File Read call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        String output = EMPTY;
        if (count < 1 || count > 50) {
            count = 1;
        }

        File file = new File(path);

        for(long i=0; i<count; i++){
            if(file.exists()) {
                output = readFileData(path);
            } else {
                return String.format(FILE_NOT_FOUND_S, path);
            }
        }
        return output;
    }


    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Reads the file by file path(String) given in the `path` request parameter")
    public String readFilePathByBody(
            @Parameter(name = "path", description = "The file path for read<br><br>Normal Case : `testfile`<br><br>Attack Case : `/etc/passwd` accessing file outside application context via absolute path.", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String path,
            @Parameter(name = "count", description = "Number of time this File Read call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
                    Integer count
    ) {
        String output = EMPTY;

        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if(StringUtils.isNotBlank(path)) {
            for (long i = 0; i < count; i++) {
                output = readFileData(path);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PATH_PARAM_NOT_FOUND);
        }
        return output;
    }

    @RequestMapping(value = "/write", method = RequestMethod.GET)
    @Operation(summary = "Writes a file by file path(String) given in the query string field name `path`. Data to be written can be supplied in the string field name `data`")
    public String writeFilePathByQueryParam(
            @Parameter(name = "path", description = "The file path to be written on", examples = {
                    @ExampleObject(summary = "Normal Case", value = "sample.txt", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "/tmp/users.txt", name = "Accessing file outside application context via absolute path.")
            })
            @RequestParam String path,
            @Parameter(name = "count", description = "Number of time this File Write call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count,
            @Parameter(name = "data", description = "The data string to be written in file specified.", examples = {
                    @ExampleObject(summary = "Normal Case", value = "sample data", name = "Normal Payload")
            })
            @RequestParam String data) {
        String output = EMPTY;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            output = writeFileData(path, data);
        }
        return output;
    }

    @RequestMapping(value = "/checkwrite", method = RequestMethod.GET)
    @Operation(summary = "Checks & writes a file by file path(String) given in the query string field name `path` only if file does not exist. Data to be written can be supplied in the string field name `data`")
    public String checkWriteFilePathByQueryParam(
            @Parameter(name = "path", description = "The file path to be written on", examples = {
                    @ExampleObject(summary = "Normal Case", value = "sample.txt", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "/tmp/users.txt", name = "Accessing file outside application context via absolute path.")
            })
            @RequestParam String path,
            @Parameter(name = "count", description = "Number of time this File Write call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count,
            @Parameter(name = "data", description = "The data string to be written in file specified.", examples = {
                    @ExampleObject(summary = "Normal Case", value = "sample data", name = "Normal Payload")
            })
            @RequestParam String data) {
        String output = EMPTY;
        if (count < 1 || count > 50) {
            count = 1;
        }
        File file = new File(path);
        for (long i = 0; i < count; i++) {
            if(!file.exists()) {
                output = writeFileData(path, data);
            } else {
                return String.format(FILE_ALREADY_EXISTS_S, path);
            }
        }
        return output;
    }


    @RequestMapping(value = "/write", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Writes a file by file path(String) given in the body by field name `path` and read back the content of the same in response. Data to be written can be supplied in the body by field name `data`")
    public String writeFilePathByBody(
            @Parameter(name = "path", description = "The file path to be written on<br><br>Normal Case : `sample.txt`<br><br>Attack Case : `/tmp/users.txt` accessing file outside application context via absolute path.", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String path,
            @Parameter(name = "count", description = "Number of time this File Write call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
                    Integer count,
            @Parameter(name = "path", description = "The data string to be written in file specified.<br><br>Normal Case : `sample data`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
            ,required = true)
                    String data
    ) {
        String output = EMPTY;

        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if(StringUtils.isNotBlank(path)) {
            for (long i = 0; i < count; i++) {
                output = writeFileData(path, data);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PATH_PARAM_NOT_FOUND);
        }
        return output;
    }

    @RequestMapping(value = "/write/blind", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Writes a file by file path(String) given in the body by field name `path`. Data to be written can be supplied in the body by field name `data`")
    public String writeFilePathByBodyBlind(
            @Parameter(name = "path", description = "The file path to be written on<br><br>Normal Case : `sample.txt`<br><br>Attack Case : `/tmp/users.txt` accessing file outside application context via absolute path.", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String path,
            @Parameter(name = "count", description = "Number of time this File Write call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
                    Integer count,
            @Parameter(name = "path", description = "The data string to be written in file specified.<br><br>Normal Case : `sample data`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String data
    ) {
        String output = EMPTY;

        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if(StringUtils.isNotBlank(path)) {
            for (long i = 0; i < count; i++) {
                output = writeFileData(path, data, false);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, PATH_PARAM_NOT_FOUND);
        }
        return output;
    }


    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @Operation(summary = "Checks if the file specified by file path(String) given in the query string field name `path` exists or not")
    public Boolean checkFilePathByQueryParam(
            @Parameter(name = "path", description = "The file path to check", examples = {
                    @ExampleObject(summary = "Normal Case", value = "testfile", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "/etc/passwd", name = "Accessing file outside application context via absolute path.")
            })
            @RequestParam String path,
            @Parameter(name = "count", description = "Number of time this check operation is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count
    ) {
        Boolean output = false;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = new File(path).exists();
        }
        return output;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @Operation(summary = "List all the files present in directory path(String) given in the query string field name `path`.")
    public String[] listFilePathByQueryParam(
            @Parameter(name = "path", description = "The directory path to list", examples = {
                    @ExampleObject(summary = "Normal Case", value = ".", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "/etc", name = "Accessing file outside application context via absolute path.")
            })
            @RequestParam String path,
            @Parameter(name = "count", description = "Number of time this list files operation is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count
    ) {
        String[] output = null;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = new File(path).list();
        }
        return output;
    }
}
