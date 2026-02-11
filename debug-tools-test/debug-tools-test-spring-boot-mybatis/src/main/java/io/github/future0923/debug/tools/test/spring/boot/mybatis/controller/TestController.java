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
package io.github.future0923.debug.tools.test.spring.boot.mybatis.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.mapper.UserMapper;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.EasyExcelTestDemo;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.User;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.model.UserVO;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.service.TestService;
import io.github.future0923.debug.tools.test.spring.boot.mybatis.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;


/**
 * @author future0923
 */
@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserService userService;
    private final TestService testService;
    private final UserMapper userMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/a")
    public User a() {
        testService.c("a");
        userService.a(null);
        User user = new User();
        user.setAge(1);
        user.getName();
        return user;
    }

    @GetMapping("/c")
    public String c() throws JsonProcessingException {
        throw new RuntimeException();
    }

    @GetMapping("/ddd")
    public List<UserVO> ddd() throws JsonProcessingException {
        return userMapper.caoayu();
    }

    @GetMapping("/testEasyExcel")
    public void testEasyExcel() throws JsonProcessingException {
        ExcelReader excelReader = EasyExcel.read(new File("/Users/wangzhongqi/Downloads/test.xlsx")).build();
        List<ReadSheet> readSheets = excelReader.excelExecutor().sheetList();
        ReadSheet readSheet = readSheets.get(0);
        readSheet.setHeadRowNumber(1);
        readSheet.setClazz(EasyExcelTestDemo.class);
        readSheet.setCustomReadListenerList(List.of(new PageReadListener<EasyExcelTestDemo>(dataList -> {
            for (EasyExcelTestDemo data : dataList) {
                System.out.println(data);
            }
        })));
        excelReader.read(readSheets);
    }
}
