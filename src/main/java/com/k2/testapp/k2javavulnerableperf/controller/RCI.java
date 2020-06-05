package com.k2.testapp.k2javavulnerableperf.controller;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/rci")
public class RCI {

    public static final String EMPTY = "";
    public static final String COUNT = "count";
    public static final String expression = "expression";
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
    public String sendResponseByQueryParam(@RequestParam String expression, @RequestParam(defaultValue = "1") long count) {
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
    public String sendResponseByBody(@RequestParam Map<String, String> paramMap) {
        String output = EMPTY;
        long count = 1;
        if(paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if (count < 1 || count > 50) {
            count = 1;
        }
        if(paramMap.containsKey(expression)) {
            for (long i = 0; i < count; i++) {
                output = parseExpression(paramMap.get(expression));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, EXPRESSION_PARAM_NOT_FOUND);
        }
        return output;
    }
}
