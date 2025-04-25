package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.User1Mapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author future0923
 */
@RestController
public class TestController {

    private final User1Mapper user1Mapper;

    public TestController(User1Mapper user1Mapper) {
    this.user1Mapper = user1Mapper;
    }

    @GetMapping("/a")
    public String a() {
        return Arrays.toString(UserService.class.getDeclaredMethods());
    }

    @GetMapping("/c")
    public String c() {
        return "c";
    }
}
