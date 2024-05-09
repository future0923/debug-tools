package io.github.future0923.debug.power.test.application.controller;

import io.github.future0923.debug.power.test.application.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author future0923
 */
@RestController
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/test")
    public String test(String name) {
        return "success:" + testService.test(name);
    }

    @GetMapping("/testDao")
    public String test(Integer id) {
        return "success:" + testService.testDao(id);
    }

}
