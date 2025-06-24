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
 * 模块描述
 *
 * @author haibo.xin
 * @date 2023/9/26 11:01
 */
@Data
public class SearchDeptBO {

    /**
     * 是否存在下级
     * @mock
     */
    private Integer flagLevel;

    /**
     * 选择的部门id集合
     * @mock []
     */
    private List<Long> deptIds;
}