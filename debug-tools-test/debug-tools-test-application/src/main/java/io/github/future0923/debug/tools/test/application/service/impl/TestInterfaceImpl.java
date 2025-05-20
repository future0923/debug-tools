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
package io.github.future0923.debug.tools.test.application.service.impl;

import io.github.future0923.debug.tools.test.application.dao.UserDao;
import io.github.future0923.debug.tools.test.application.domain.entity.User;
import io.github.future0923.debug.tools.test.application.service.TestInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author future0923
 */
@Service
public class TestInterfaceImpl implements TestInterface {

    @Autowired
    private UserDao userDao;

    @Override
    public void test(String name) {
        List<User> users = userDao.selectByName(name);
        users.forEach(System.out::println);
    }
}
