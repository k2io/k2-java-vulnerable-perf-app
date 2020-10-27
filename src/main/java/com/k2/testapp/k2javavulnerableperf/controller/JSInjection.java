package com.k2.testapp.k2javavulnerableperf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

@RestController
@RequestMapping("/js")
@Tag(name = "JS Injection Controller", description = "APIs performing JavaScript evaluation using Nashorn engine. Script can be modified to perform JS injections.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "JS Injection successful")
})
public class JSInjection {

    public static final String NASHORN = "nashorn";
    public static final String ERROR_WHILE_EVALUATING_SCRIPT_S_ERROR_S_CAUSED_BY_S = "Error while evaluating script : %s : error: %s, caused by : %s";
    public static final String SCRIPT_PARAMETER_NOT_FOUND = "Script parameter not found";
    public static final String JS_SCRIPT = "var hello = function(){  var output = \"Hello %s\"; return output;  };  hello(); ";

    private String evaluate(String script) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName(NASHORN);
        try {
            Object output = engine.eval(String.format(JS_SCRIPT, script));
            return new ObjectMapper().writeValueAsString(output);
        } catch (ScriptException | JsonProcessingException e) {
            return String.format(ERROR_WHILE_EVALUATING_SCRIPT_S_ERROR_S_CAUSED_BY_S, script, e.getMessage(), e.getCause());
        }
    }

    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Reads the script parameter and place the param into script's var name output.")
    public String userProvidedScript(
            @Parameter(name = "script param", description = "The script part to be concatenated with output<br><br>Normal Case : `K2` <br><br>Attack Case : `k2\"; print(\"hacked\"); output=\"hacked by k2\"; var sbc=\"ac` modifies the script, overrides output and print gibberish info.", examples = {
                    @ExampleObject(summary = "Normal Case", value = "K2", name = "Normal Payload"),
                    @ExampleObject(summary = "Attack Case", value = "k2\"; print(\"hacked\"); output=\"hacked by k2\"; var sbc=\"ac", name = "modifies the script, override output and print gibberish info.")
            })
            String script,
            @Parameter(name = "count", description = "Number of time this script evaluation call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
            Integer count) {
        String output = StringUtils.EMPTY;
        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(script)) {
            for (long i = 0; i < count; i++) {
                output = evaluate(script);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, SCRIPT_PARAMETER_NOT_FOUND);
        }
        return output;
    }
}
