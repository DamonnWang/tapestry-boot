package org.example.tapestry.admin;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 */
@EnableDubbo
@SpringBootApplication
public class TapestryBootApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TapestryBootApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
