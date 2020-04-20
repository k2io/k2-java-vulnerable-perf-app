package com.k2.testapp.k2javavulnerableperf.controller;

import com.k2.testapp.k2javavulnerableperf.model.Billionaires;
import com.k2.testapp.k2javavulnerableperf.repository.BillionairesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/sql")
public class SQL {

    public static final String NAME = "name";
    public static final String COUNT = "count";
    public static final String ID = "id";

    @Autowired
    private BillionairesRepository dataRepository;

    @RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Billionaires getBillionaireById(@PathVariable long id, @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1) {
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
        if (count < 1) {
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
        long count = Long.parseLong(paramMap.get(COUNT));
        Billionaires billionaires = null;
        if (count < 1) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.findById(Long.parseLong(paramMap.get(ID))).orElseGet(Billionaires::new);
        }
        return billionaires;
    }

    @RequestMapping(value = "/firstname/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Billionaires getBillionaireByName(@PathVariable String name, @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1) {
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
        if (count < 1) {
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
        long count = Long.parseLong(paramMap.get(COUNT));
        Billionaires billionaires = null;
        if (count < 1) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.getBillionaireByName(paramMap.get(NAME));
        }
        return billionaires;
    }

    @RequestMapping(value = "/save", method = RequestMethod.GET)
    @ResponseBody
    public Billionaires saveBillionaire(@Valid Billionaires billionaireToSave,
                                        @RequestParam(defaultValue = "1") long count) {
        Billionaires billionaires = null;
        if (count < 1) {
            count = 1;
        }
        for (long i = 0; i < count; i++) {
            billionaires = dataRepository.save(billionaireToSave);
            dataRepository.delete(billionaires);
        }
        return billionaires;
    }
}
