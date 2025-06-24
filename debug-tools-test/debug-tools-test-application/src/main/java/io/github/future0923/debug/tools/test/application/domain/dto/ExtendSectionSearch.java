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