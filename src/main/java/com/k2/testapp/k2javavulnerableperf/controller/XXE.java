package com.k2.testapp.k2javavulnerableperf.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;


@RestController
@RequestMapping("/xxe")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Request served successfully.")
})
@Tag(name = "XXE Controller", description = "APIs doing some risky un-validated XML processing.")
public class XXE {
    private static final String ARG_PARAM_NOT_FOUND = "arg param not found";
    public static final String ERROR_S_S = "Error : %s : %s";

    private static final String XML_DATA= "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n" +
            "<document xmlns:xi=\"http://www.w3.org/2001/XInclude\"><p>Hello : %s.</p></document>";


    private static Document getDocument(String data) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setXIncludeAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(data)));
        return doc;
    }

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "Parses the predefined xml doc with user defined xml argument given in `arg` parameter & reverts with response of the parsing as XML doc.")
    public String sendResponseByQueryParam(
            @Parameter(name = "arg", description = "User input to be used in XML doc", examples = {
                    @ExampleObject(summary = "Normal Case", value = "User", name = "Providing a simple string to be inserted into predefined doc."),
                    @ExampleObject(summary = "Attack Case", value = "Content of \"/etc/passwd\" :: <xi:include href=\"file:///etc/passwd\" parse=\"text\"/>", name = "This payload tried to exploit include directive from current namespace & tries to include the content of /etc/passwd file to see it in the response")
            })
            @RequestParam String arg) {
        String output = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(arg)) {
            output = String.format(XML_DATA, arg);
            try {
                Document doc = getDocument(output);
                DOMSource source = new DOMSource(doc);
                StringWriter writer = new StringWriter();
                StreamResult result = new StreamResult(writer);
                TransformerFactory.newInstance().newTransformer().transform(source, result);
                output = writer.toString();
            } catch (Exception e) {
                return String.format(ERROR_S_S, e.getMessage(), e.getCause());
            }

        } else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, ARG_PARAM_NOT_FOUND);
        }
        return output;
    }
}
