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
package io.github.future0923.debug.tools.test.application.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 区间类检索
 *
 * @author haibo.xin
 * @date 2023/2/18 9:20
 */
@Data
public class ExtendSectionSearch<T> {

    /**
     * 区间标识
     *
     * @mock
     */
    String searchKey;

    /**
     * 区间最小值
     *
     * @mock
     */
    T min;

    /**
     * 区间最大值
     *
     * @mock
     */
    T max;

    /**
     * 用来接收枚举
     *
     * @mock
     */
    Object code;

    /**
     * 时间区间
     *
     * @mock [2023-12-12 12:12:12]
     */
    List<T> queryDate;
}