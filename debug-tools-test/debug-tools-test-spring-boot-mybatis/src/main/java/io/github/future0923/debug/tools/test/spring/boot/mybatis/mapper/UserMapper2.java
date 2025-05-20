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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author future0923
 */
@Mapper
public interface UserMapper2 {

    List<User> selectByNameAndAge(@Param("name") String name, @Param("age") Integer age);

    @Select("select * from dp_user")
    List<User> saddasdas();

    @Select("select * from dp_user limit1")
    List<User> limit1();


    @Select("select * from dp_user limit1")
    List<User> ddd();

    @Select("select * from dp_user limit 1")
    List<User> aaa();
}
