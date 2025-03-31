package io.github.future0923.debug.tools.test.spring.boot.three.controller;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author future0923
 */
@RestController
public class TestController {

    @GetMapping
    public String test(ServerHttpRequest request, ServerHttpResponse response, String name, Integer age) {
        return "name = " + name + ", age = " + age;
    }

    @GetMapping("/b")
    public String test2() {
        return "b";
    }
}
