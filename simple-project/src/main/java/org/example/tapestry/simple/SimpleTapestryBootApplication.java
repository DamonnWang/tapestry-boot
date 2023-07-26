package org.example.tapestry.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SimpleTapestryBootApplication {
    public static void main(String[] args) {
        // new SpringApplicationBuilder(SimpleTapestryBootApplication.class)
        //         .web(WebApplicationType.SERVLET)
        //         .run(args);
        SpringApplication.run(SimpleTapestryBootApplication.class, args);
    }

}
