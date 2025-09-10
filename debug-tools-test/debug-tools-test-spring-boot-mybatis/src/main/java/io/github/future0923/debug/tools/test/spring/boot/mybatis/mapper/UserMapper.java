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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author future0923
 */
@Mapper
//@DS("bbb")
public interface UserMapper {

    List<User> selectByNameAndAge(@Param("name") String name, @Param("age") Integer age);

    @Select("select * from dp_user")
    List<User> saddasdas();

    @Select("select * from dp_user limit1")
    List<User> limit1();


    @Select("select * from dp_user limit1")
    List<User> ddd();

    @Select("select * from dp_user limit 1")
    List<User> aaa();

    @Select("select * from dp_user where update_date > #{updateDate}")
    List<User> findByUpdateData(@Param("updateDate") LocalDate updateDate);

    @Select("update dp_user set name = #{name},update_at = #{updateAt} where id = #{id}")
    void updateNameById(@Param("name") String name, @Param("updateAt") LocalDateTime updateAt, @Param("id") Long id);

    @Update("update dp_user set update_date = #{updateDate} where id = 1")
    void updateDate(@Param("updateDate") LocalDate updateDate);

    @SelectProvider(type = UserProvider.class, method = "testProvider")
    List<User> testProvider();
}
