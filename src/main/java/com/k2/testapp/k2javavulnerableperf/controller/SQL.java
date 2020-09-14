package com.k2.testapp.k2javavulnerableperf.controller;

import com.k2.testapp.k2javavulnerableperf.model.Billionaires;
import com.k2.testapp.k2javavulnerableperf.repository.BillionairesRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sql")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "SQL operation completed successfully.")
})
@Tag(name = "SQL Controller", description = "APIs doing SQL calls via Java Persistence API but have some intentional vulnerabilities.")
public class SQL {

    public static final String COUNT = "count";
    public static final String ID = "id";
    public static final String FIRST_NAME_PARAM_NOT_FOUND = "firstName param not found";
    public static final String ID_PARAM_NOT_FOUND = "Id param not found";

    @Autowired
    private BillionairesRepository dataRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Does an SQL query to get record by id(int) given in the `id` path parameter")
    public Billionaires getBillionaireById(@Parameter(name = "id", description = "The integer ID for the record search", examples = {
            @ExampleObject(summary = "Normal Case", value = "1", name = "Normal Payload")
            }) @PathVariable long id,
               @Parameter(name = "count", description = "Number of time this SQL call is executed", hidden = true)
               @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.findById(id).orElseGet(Billionaires::new);
        }
        return billionaires;
    }

    @RequestMapping(value = "/id", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Does an SQL query to get record by id(int) given in the `id` parameter")
    public Billionaires getBillionaireByIdQueryParam(
            @Parameter(name = "id", description = "The integer ID for the record search", examples = {
                    @ExampleObject(summary = "Normal Case", value = "1", name = "Normal Payload")
            })
            @RequestParam long id,
            @Parameter(name = "count", description = "Number of time this SQL call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.findById(id).orElseGet(Billionaires::new);
        }
        return billionaires;
    }

    @RequestMapping(value = "/id", method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    @Operation(summary = "Does an SQL query to get record by id(int) given in the `id` parameter")
    public Billionaires getBillionaireByIdBody(
            @Parameter(name = "id", description = "The integer ID for the record search<br><br>Normal Case : `1`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String id,
            @Parameter(name = "count", description = "Number of time this SQL call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
                    Integer count

    ) {
        Billionaires billionaires = null;
        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if (StringUtils.isNotBlank(id)) {
            for (long i = 0; i < count; i++) {
                billionaires = dataRepository.findById(Long.parseLong(id)).orElseGet(Billionaires::new);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ID_PARAM_NOT_FOUND);
        }
        return billionaires;
    }

    @RequestMapping(value = "/firstname/{name}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Does an SQL query to get record by firstname(string) given in the `name` path parameter")
    public Billionaires getBillionaireByName(
            @Parameter(name = "name", description = "The firstname for the record search", examples = {
                    @ExampleObject(summary = "Normal Case", value = "Aliko", name = "Normal Payload")
            })
            @PathVariable String name,
            @Parameter(name = "count", description = "Number of time this SQL call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.getBillionaireByName(name);
        }
        return billionaires;
    }

    @RequestMapping(value = "/firstname", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Does an SQL query to get record by firstname(string) given in the `name` parameter")
    public Billionaires getBillionaireByNameQueryParam(
            @Parameter(name = "name", description = "The firstname for the record search", examples = {
                    @ExampleObject(summary = "Normal Case", value = "Aliko", name = "Normal Payload")
            })
            @RequestParam String name,
            @Parameter(name = "count", description = "Number of time this SQL call is executed", hidden = true)
            @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.getBillionaireByName(name);
        }
        return billionaires;
    }

    @RequestMapping(value = "/firstname", method = RequestMethod.POST
            , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    @Operation(summary = "Does an SQL query to get record by firstname(string) given in the `firstname` parameter")
    public List<Billionaires> getBillionaireByNameBody(
            @Parameter(name = "firstName", description = "The firstname for the record search<br><br>Attack Case  : `Aliko' OR '1'='1`<br><br>Normal Case : `Aliko`", in= ParameterIn.QUERY, style = ParameterStyle.FORM
                    ,required = true)
                    String firstName,
            @Parameter(name = "count", description = "Number of time this SQL call is executed, Optional & defaults to `1`.", in= ParameterIn.QUERY, style = ParameterStyle.FORM)
                    Integer count
    ) {
        List<Billionaires> billionaires = null;
        if (count == null || count < 1 || count > 50) {
            count = 1;
        }
        if (StringUtils.isNotBlank(firstName)) {
            for (long i = 0; i < count; i++) {
                billionaires = jdbcTemplate.query("SELECT * FROM billionaires WHERE first_name = '" + firstName + "'",
                        new BeanPropertyRowMapper<Billionaires>(Billionaires.class));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, FIRST_NAME_PARAM_NOT_FOUND);
        }
        return billionaires;
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    @Operation(summary = "Does an SQL query to save given record")

    public Billionaires saveBillionaire( @Parameter(name = "firstName", description = "Firstname for the record save", examples = {
                 @ExampleObject(summary = "Normal Case", value = "Aliko", name = "Normal Payload")
         }) @RequestParam String firstName,
         @Parameter(name = "lastName", description = "LastName for the record save", examples = {
                 @ExampleObject(summary = "Normal Case", value = "Dangote", name = "Normal Payload")
         }) @RequestParam String lastName,
         @Parameter(name = "career", description = "Career for the record save", examples = {
                 @ExampleObject(summary = "Normal Case", value = "Billionaire Industrialist", name = "Normal Payload")
         }) @RequestParam String career,
         @Parameter(name = "count", description = "Number of time this SQL call is executed", hidden = true)
         @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = new Billionaires();
        billionaires.setFirstName(firstName);
        billionaires.setLastName(lastName);
        billionaires.setCareer(career);

        if (count < 1 || count > 50) {
            count = 1;
        }

        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.save(billionaires);
            dataRepository.delete(billionaires);
        }
        return billionaires;
    }
}
