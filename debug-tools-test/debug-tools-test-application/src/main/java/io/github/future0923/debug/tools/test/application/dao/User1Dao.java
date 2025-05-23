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
package io.github.future0923.debug.tools.test.application.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.future0923.debug.tools.test.application.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface User1Dao extends BaseMapperPlus<User> {

    @Select("select * from user where name = #{name} and age = #{age}")
    List<User> selectByNameAndAge(
            @Param("name") String name,
            @Param("age") Integer age);

    default List<User> selectByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getName, name);
        return selectList(queryWrapper);
    }
    //
    @Select("select * from dp_user where name = #{name}")
    List<User> bbb(@Param("name") String name);
}
