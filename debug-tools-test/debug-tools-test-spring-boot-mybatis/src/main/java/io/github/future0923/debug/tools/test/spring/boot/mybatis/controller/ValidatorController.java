/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

        @Length(min = 6, max = 20, message = "用户名长度在6-20个字符之间")
        @NotBlank
        private String name;

        @Min(value = 19, message = "年龄不能小于19")
        @NotNull
        private Integer age;
    }

    @PostMapping("/validator")
    public String name(@Validated @RequestBody DTO dto) {
        return dto.getName();
    }
}
