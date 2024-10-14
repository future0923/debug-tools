package io.github.future0923.debug.tools.test.application;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author future0923
 */
@Slf4j
@SpringBootApplication
@MapperScan("io.github.future0923.debug.tools.test.application.dao")
public class DebugToolsTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DebugToolsTestApplication.class, args);
    }
}
