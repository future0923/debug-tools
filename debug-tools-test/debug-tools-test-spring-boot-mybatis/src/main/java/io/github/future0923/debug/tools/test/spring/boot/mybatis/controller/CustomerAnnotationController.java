package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.annotation.OpenApis;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@OpenApis
@RequestMapping("customer")
public class CustomerAnnotationController {

    @PostMapping("customerPost")
    public void customerPost() {
    }
    @GetMapping("customerGet")
    public void customerGet() {
    }
}
