package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/rci")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request served successfully.")
})
@Tag(name = "RCI Controller", description = "APIs doing unsecured parsing of OGNL expressions & reverting back response of the same.")
public class RCI {

    public static final String EMPTY = "";
    public static final String COUNT = "count";
    public static final String ERROR_WHILE_PARSING_EXPRESSION_S_S_S = "Error while parsing expression : %s :: %s : %s";
    public static final String EXPRESSION_PARAM_NOT_FOUND = "expression param not found";

    private String parseExpression(String expression){
        String response = EMPTY;
        try {
            Object expr = Ognl.parseExpression(expression);
            OgnlContext ctx = new OgnlContext();
            Object value = Ognl.getValue(expr, ctx);
            response = value.toString();
        } catch (Exception e) {
            return String.format(ERROR_WHILE_PARSING_EXPRESSION_S_S_S, expression, e.getMessage(), e.getCause());
        }
        return response;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Parses the expression given in `expression` parameter & reverts with response of the same.")

    public String sendResponseByQueryParam(
            @Parameter(name = "expression", description = "Expression to be evaluated", examples = {
                    @ExampleObject(summary = "Normal Case", value = "@java.lang.String@format(\"%1$tY/%1$tm/%1$td\",new java.util.Date())", name = "This payload does a simple current date parsing using OGNL"),
                    @ExampleObject(summary = "Attack Case", value = "@Runtime@getRuntime().exec('touch /tmp/hacked')", name = "This payload uses the OGNL API to spawn a shell command to create a file /tmp/hacked")
            })
            @RequestParam String expression,
            @Parameter(name = "count", description = "Number of time this call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        String output = EMPTY;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for(long i=0; i<count; i++){
            output = parseExpression(expression);
        }
        return output;
    }

    @RequestMapping(method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @Operation(summary = "Parses the expression given in `expression` parameter & reverts with response of the same.")
    public String sendResponseByBody(
            @Parameter(name = "expression", description = "Expression to be evaluated<br><br>Normal Case : `@java.lang.String@format(\"%1$tY/%1$tm/%1$td\",new java.util.Date())` which does a simple current date parsing using OGNL<br><br>Attack Case : `@Runtime@getRuntime().exec('touch /tmp/hacked')` which uses the OGNL API to spawn a shell command to create a file /tmp/hacked", in= ParameterIn.DEFAULT, style = ParameterStyle.FORM
                    ,required = true)
                    String expression,

            @Parameter(name = "count", description = "Number of time this call is executed, Optional & defaults to `1`.", in= ParameterIn.DEFAULT, style = ParameterStyle.FORM)
                    Integer count
    ) {
        String output = EMPTY;
        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if(StringUtils.isNotBlank(expression)) {
            for (long i = 0; i < count; i++) {
                output = parseExpression(expression);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, EXPRESSION_PARAM_NOT_FOUND);
        }
        return output;
    }
}
