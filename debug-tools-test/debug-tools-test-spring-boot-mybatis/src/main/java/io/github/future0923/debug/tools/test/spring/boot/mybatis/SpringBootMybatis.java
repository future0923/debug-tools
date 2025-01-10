package io.github.future0923.debug.tools.test.spring.boot.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author future0923
 */
@SpringBootApplication
@MapperScan("io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper")
public class SpringBootMybatis {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootMybatis.class, args);
    }
}
