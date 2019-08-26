package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能：
 *
 * @author Damon
 * @since 2019-04-15 15:08
 */
@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        return "hello world!";
    }
}
