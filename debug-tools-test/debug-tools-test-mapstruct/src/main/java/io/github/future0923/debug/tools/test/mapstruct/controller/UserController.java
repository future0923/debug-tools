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
