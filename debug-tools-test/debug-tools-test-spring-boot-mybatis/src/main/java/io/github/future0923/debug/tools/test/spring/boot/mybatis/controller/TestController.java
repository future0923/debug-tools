/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
