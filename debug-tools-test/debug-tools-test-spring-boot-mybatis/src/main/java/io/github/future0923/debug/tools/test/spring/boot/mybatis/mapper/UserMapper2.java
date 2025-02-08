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
