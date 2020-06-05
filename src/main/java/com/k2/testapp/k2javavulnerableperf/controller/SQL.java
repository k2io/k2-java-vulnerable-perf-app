package com.k2.testapp.k2javavulnerableperf.controller;

import com.k2.testapp.k2javavulnerableperf.model.Billionaires;
import com.k2.testapp.k2javavulnerableperf.repository.BillionairesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/sql")
public class SQL {

    public static final String FIRST_NAME = "firstName";
    public static final String COUNT = "count";
    public static final String ID = "id";
    public static final String FIRST_NAME_PARAM_NOT_FOUND = "firstName param not found";
    public static final String ID_PARAM_NOT_FOUND = "Id param not found";

    @Autowired
    private BillionairesRepository dataRepository;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Billionaires getBillionaireById(@PathVariable long id, @RequestParam(defaultValue = "1") long count) {
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
    public Billionaires getBillionaireByIdQueryParam(@RequestParam long id, @RequestParam(defaultValue = "1") long count) {
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
    public Billionaires getBillionaireByIdBody(@RequestParam Map<String, String> paramMap) {
        Billionaires billionaires = null;
        long count = 1;
        if(paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if (count < 1 || count > 50) {
            count = 1;
        }
        if(paramMap.containsKey(ID)) {
            for (long i = 0; i < count; i++) {
                billionaires = dataRepository.findById(Long.parseLong(paramMap.get(ID))).orElseGet(Billionaires::new);
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ID_PARAM_NOT_FOUND);
        }
        return billionaires;
    }

    @RequestMapping(value = "/firstname/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Billionaires getBillionaireByName(@PathVariable String name, @RequestParam(defaultValue = "1") long count) {
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
    public Billionaires getBillionaireByNameQueryParam(@RequestParam String name, @RequestParam(defaultValue = "1") long count) {
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
    public Billionaires getBillionaireByNameBody(@RequestParam Map<String, String> paramMap) {
        Billionaires billionaires = null;
        long count = 1;
        if(paramMap.containsKey(COUNT)) {
            count = Long.parseLong(paramMap.get(COUNT));
        }
        if (count < 1 || count > 50) {
            count = 1;
        }
        if(paramMap.containsKey(FIRST_NAME)) {
            for (long i = 0; i < count; i++) {
                billionaires = dataRepository.getBillionaireByName(paramMap.get(FIRST_NAME));
            }
        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, FIRST_NAME_PARAM_NOT_FOUND);
        }
        return billionaires;
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    @Transactional
    public Billionaires saveBillionaire(@Valid Billionaires billionaireToSave,
                                        @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1 || count > 50) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.save(billionaireToSave);
            dataRepository.delete(billionaires);
        }
        return billionaires;
    }
}
