package com.k2.testapp.k2javavulnerableperf;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Calendar;
import java.util.Date;

@SpringBootApplication
public class K2JavaVulnerablePerfApplication {

    public static void main(String[] args) {
        SpringApplication.run(K2JavaVulnerablePerfApplication.class, args);
    }


    @Bean
    public OpenAPI customOpenAPI(@Value("${info.app.version:unknown}") String appVersion) {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("K2 Java Vulnerable Application")
                                .version(appVersion)
                                .description(
                                "A vulnerability testing web application developed by <a href='https://www.k2io.com/'>K2 Cyber Security</a> to assess it Next-Gen Java Runtime protection technology." +
                                        String.format("<br><br><b>© %s, K2 Cyber Security Inc. | All Rights Reserved | <a href=\"https://www.k2io.com/term-and-condition/\">Terms & Condition</a></b>", Calendar.getInstance().get(Calendar.YEAR))
                                ).license(new License().name("Commercial").url("https://github.com/k2io/k2-java-vulnerable-perf-app/blob/master/LICENSE"))

                );
    }
}
