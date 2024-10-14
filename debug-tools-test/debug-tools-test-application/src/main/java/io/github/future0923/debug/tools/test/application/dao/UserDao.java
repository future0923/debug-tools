package io.github.future0923.debug.tools.test.application.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.github.future0923.debug.tools.test.application.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapperPlus<User> {

    @Select("select * from user where name = #{name} and age = #{age}")
    List<User> selectByNameAndAge(
            @Param("name") String name,
            @Param("age") Integer age);

    default List<User> selectByName(String name) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(User::getName, name);
        return selectList(queryWrapper);
    }
}
