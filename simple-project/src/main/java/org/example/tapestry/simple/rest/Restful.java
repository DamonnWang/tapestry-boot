package org.example.tapestry.simple.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Damon
 * @date 2023/6/28
 **/
@RestController
@RequestMapping("/rest/")
public class Restful {

    @GetMapping("/")
    public String index() {
        return "ok";
    }
}
