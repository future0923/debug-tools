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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.service;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.User1Mapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.UserMapper;
import org.springframework.stereotype.Service;

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
        return userMapper.aaa().toString();
    }

    public String a() {
        System.out.println(2222222);
        return "1";
    }
}
