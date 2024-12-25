package io.github.future0923.debug.tools.test.application.controller;

import io.github.future0923.debug.tools.test.application.dao.UserDao;
import io.github.future0923.debug.tools.test.application.service.Test1Service;
import io.github.future0923.debug.tools.test.application.service.Test2Service;
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

    private final Test2Service test2Service;

    private final UserDao userDao;

    @GetMapping("/hot")
    public String ok() {
        return "asd";
    }


    @GetMapping("/hot1")
    public String ok1() {
        return "asd1";
    }

    @GetMapping("/hot2")
    public String ok2() {
        return "asd1";
    }

    @GetMapping("/hot3")
    public String ok3() {
        return test2Service.test();
    }

    @GetMapping("/insertBatchSomeColumn")
    public String insertBatchSomeColumn() {
        return testService.insertBatchSomeColumn();
    }

    @GetMapping("/testDao")
    public String test(Integer id) {
        return "success:" + testService.testDao(id);
    }

}
