package io.github.future0923.debug.tools.test.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author future0923
 */
@Configuration
public class TestConfig {

    @Bean
    public TestBean testBean() {
        return new TestBean();
    }

    public static class TestBean {
        public String test() {
            return "test";
        }
    }
}
