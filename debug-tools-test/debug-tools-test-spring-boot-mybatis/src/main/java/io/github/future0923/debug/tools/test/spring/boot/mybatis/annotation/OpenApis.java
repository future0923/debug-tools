package io.github.future0923.debug.tools.test.spring.boot.mybatis.annotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface OpenApis {
}
