package io.github.future0923.debug.tools.test.spring.boot.mybatis.service;

import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.UserMapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.UserMapper2;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author future0923
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    private final UserMapper2 userMapper2;

    public List<User> selectByNameAndAge(String name, Integer age) {
        return userMapper.selectByNameAndAge(name, age);
    }

    public String a() {
        return "a";
    }

    public String b() {
        return "b";
    }
}
