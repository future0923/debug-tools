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
package io.github.future0923.debug.tools.test.spring.boot.three.controller;

import io.github.future0923.debug.tools.test.spring.boot.three.entity.User;
import io.github.future0923.debug.tools.test.spring.boot.three.service.CglibService;
import io.github.future0923.debug.tools.test.spring.boot.three.service.UserService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author future0923
 */
@RestController
public class TestController {

    private final UserService userService;

    private final CglibService cglibService;

    public TestController(UserService userService, CglibService cglibService) {
        this.userService = userService;
        this.cglibService = cglibService;
    }

    @GetMapping
    public String test(ServerHttpRequest request, ServerHttpResponse response, String name, Integer age) {
        return "name = " + name + ", age = " + age;
    }

    @GetMapping("/b")
    public String test2() {
        User user = new User();
        user.setName("1");
        user.setAge1(1);
        user.setVersion(0);
        //userService.saveBatch(Collections.singletonList(user));
        cglibService.test();
        return "b";
    }
}
