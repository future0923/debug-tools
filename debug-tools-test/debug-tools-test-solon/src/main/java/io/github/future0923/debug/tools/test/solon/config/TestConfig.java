package io.github.future0923.debug.tools.test.solon.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;

/**
 * @author future0923
 */
@Configuration
public class TestConfig {

    public static class TestConfigBean {

    }

    @Bean
    public TestConfigBean testConfigBean() {
        return new TestConfigBean();
    }
}
