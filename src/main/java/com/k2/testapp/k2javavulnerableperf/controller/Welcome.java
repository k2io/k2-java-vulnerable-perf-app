package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Hidden;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Hidden
public class Welcome {

    private static String WELCOME_MESSAGE = "<h2 class=\"code-line\" data-line-start=0 data-line-end=1 ><a id=\"K2_Java_Vulnerable_Application_0\"></a>K2 Java Vulnerable Application</h2>\n" +
            "<p class=\"has-line-data\" data-line-start=\"1\" data-line-end=\"3\">A vulnerability testing web application developed by <a href=\"https://www.k2io.com/\">K2 Cyber Security</a> to assess it Next-Gen Java Runtime protection technology.<br>\n" +
            "The application is a Spring Boot based web application using embedded H2 DB as datastore.</p>\n" +
            "<p class=\"has-line-data\" data-line-start=\"4\" data-line-end=\"5\">As this application does not come with a UI, please refer the interactive OpenAPI 3 documentations at <a href=\"./docs\">docs</a> to interact with the application APIs.</p>";

    @RequestMapping(method = RequestMethod.GET)
    public String welcome() {
        return WELCOME_MESSAGE;
    }

}
