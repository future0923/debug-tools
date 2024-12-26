package io.github.future0923.debug.tools.test.application.controller;

import io.github.future0923.debug.tools.test.application.service.AddService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author future0923
 */
@RestController
@RequiredArgsConstructor
public class AddController {

    private final AddService addService;

    @GetMapping("/add2")
    public String add1() {
        return "add2";
    }

    @GetMapping("/add1")
    public String add2() {
        return addService.a();
    }
}
