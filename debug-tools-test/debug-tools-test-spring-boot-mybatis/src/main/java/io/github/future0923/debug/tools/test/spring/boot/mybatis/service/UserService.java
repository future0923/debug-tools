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
        return userMapper.toString();
    }

    public String a() {
        System.out.println(2222222);
        return "1";
    }
}
