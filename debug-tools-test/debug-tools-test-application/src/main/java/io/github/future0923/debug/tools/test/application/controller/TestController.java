package io.github.future0923.debug.tools.test.application.controller;

import io.github.future0923.debug.tools.test.application.service.Test1Service;
import io.github.future0923.debug.tools.test.application.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author future0923
 */
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    private final Test1Service test1Service;

    @GetMapping("/hot")
    public String ok1() {
        return "asd";
    }

    //
    //@GetMapping("/hot1")
    //public String ok2() {
    //    return "asd";
    //}

    @GetMapping("/insertBatchSomeColumn")
    public String insertBatchSomeColumn() {
        return testService.insertBatchSomeColumn();
    }

    @GetMapping("/testDao")
    public String test(Integer id) {
        return "success:" + testService.testDao(id);
    }

}
