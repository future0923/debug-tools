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
public interface User1Mapper {

    @Select("select * from dp_user limit 1")
    List<User> aaa();

    @Select("select * from dp_user limit 1")
    List<User> c();

    @Select("select * from dp_user limit 1")
    List<User> dd();
}
