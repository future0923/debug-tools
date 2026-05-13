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

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author future0923
 */
@RestController
public class ValidatorController {

    @Data
    public static class DTO {

        /**
         * 名字
         */
        @Length(min = 6, max = 20, message = "用户名长度在6-20个字符之间")
        @NotBlank
        private String name;

        /**
         * 年龄
         */
        @Min(value = 19, message = "年龄不能小于19")
        @NotNull
        private Integer age;

        private Test test;
    }

    public enum Test {

        /**
         * 男
         */
        NAN,

        /**
         * 女
         */
        NV
    }

    @Data
    public static class Parent {

        /**
         * 属性1
         */
        private String f1;
    }

    @Data
    public static class Son extends Parent {

        /**
         * 属性2
         */
        private String f2;

        private Long en;
    }

    /**
     *
     * @param dto 动态
     * @param debug 打算
     * @return
     */
    @PostMapping("/validator")
    public String name(@Validated @RequestBody DTO dto, String debug, Son son) {
        return dto.getName();
    }
}
