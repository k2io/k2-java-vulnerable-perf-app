package com.k2.testapp.k2javavulnerableperf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {                                    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("K2 Java Vulnerable Application Documentation")
                        .description("Vulnerability detection testing webapp for K2 Java Collector")
                        .version("1.0.0")
                        .license("Apache License Version 2.0")
                        .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
                        .build())
          .select()
          .apis(RequestHandlerSelectors.any())
          .paths(PathSelectors.any())
          .build();                                           
    }
}