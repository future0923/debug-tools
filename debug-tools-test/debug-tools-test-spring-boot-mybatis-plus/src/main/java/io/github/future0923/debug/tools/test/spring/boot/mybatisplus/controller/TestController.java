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
package io.github.future0923.debug.tools.test.spring.boot.mybatisplus.controller;

import io.github.future0923.debug.tools.test.spring.boot.mybatisplus.entity.User;
import io.github.future0923.debug.tools.test.spring.boot.mybatisplus.service.UserServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


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

    @PostMapping("/file/batch")
    public String uploadFiles(@RequestParam(required = false) MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return "未接收到文件或文件为空";
        }
        return "上传成功，文件数量：" + files.length;
    }

    @PostMapping("/file/single")
    public String uploadFile(@RequestParam MultipartFile file) {
        return "上传成功，文件数量：" + file.getName();
    }

    @GetMapping("/test/param12")
    public String testParam(@RequestParam("name123") String name){
        System.out.println(name);
        return name;
    }
}
