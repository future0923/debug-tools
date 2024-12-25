package io.github.future0923.debug.tools.test.application.service;

import org.springframework.stereotype.Service;

/**
 * @author future0923
 */
@Service
public class Test2Service {

    public String test() {
        System.out.println("test1");
        return "Test2Service.test";
    }
}
