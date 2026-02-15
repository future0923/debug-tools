package io.github.future0923.debug.tools.test.mapstruct.controller;

import io.github.future0923.debug.tools.test.mapstruct.domain.UserEntity;
import io.github.future0923.debug.tools.test.mapstruct.mapper.UserMapper;
import io.github.future0923.debug.tools.test.mapstruct.model.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/convert")
    public UserVO convert() {
        UserEntity entity = new UserEntity();
        entity.setId(9527L);
        entity.setUsername("MapStruct_UserAAA");
        entity.setPassword("hidden_pwd");
        entity.setCreateTime(LocalDateTime.now());

        return userMapper.entityToVo(entity);
    }
}
