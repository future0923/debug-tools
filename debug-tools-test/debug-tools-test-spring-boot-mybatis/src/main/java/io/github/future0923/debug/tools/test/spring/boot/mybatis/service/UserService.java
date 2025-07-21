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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.service;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.User1Mapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.UserMapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
@Service
public class UserService {

    private final UserMapper userMapper;

    private final User1Mapper user1Mapper;

    public UserService(UserMapper userMapper, User1Mapper user1Mapper) {
        this.userMapper = userMapper;
        this.user1Mapper = user1Mapper;
    }


    public String c() {
        System.out.println("11111111");
        userMapper.aaa();
        return null;
    }

    public String a(Map<String, List<User>> userMap) {
        System.out.println(2222222);
        return "1";
    }
}
