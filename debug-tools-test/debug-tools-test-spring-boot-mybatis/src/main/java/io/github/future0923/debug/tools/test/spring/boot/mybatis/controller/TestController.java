/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.User1Mapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author future0923
 */
@RestController
public class TestController {

    private final User1Mapper user1Mapper;

    public TestController(User1Mapper user1Mapper) {
    this.user1Mapper = user1Mapper;
    }

    @GetMapping("/a")
    public String a() {
        return Arrays.toString(UserService.class.getDeclaredMethods());
    }

    @GetMapping("/c")
    public String c() {
        return "c";
    }
}
