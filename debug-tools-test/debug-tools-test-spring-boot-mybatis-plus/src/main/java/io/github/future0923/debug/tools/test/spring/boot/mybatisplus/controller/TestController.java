/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.test.spring.boot.mybatisplus.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.future0923.debug.tools.test.spring.boot.mybatisplus.entity.User;
import io.github.future0923.debug.tools.test.spring.boot.mybatisplus.service.UserServiceImpl;

/**
 * @author future0923
 */
@RestController
public class TestController {

    private final UserServiceImpl userService;

    public TestController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("test")
    public String test() {
        return "hello world";
    }

    @GetMapping("test2")
    public User test2() {
        return userService.getById(1);
    }

    @GetMapping("test3")
    public boolean test3() {
        User entity = new User();
        entity.setId(9);
        entity.setName("test");
        entity.setAge(18);
        entity.setVersion(1);
        entity.setMetaData(Map.of("key", "1'2333"));

        return userService.save(entity);
    }
}
