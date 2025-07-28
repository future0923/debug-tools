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
package io.github.future0923.debug.tools.common.dto;

import lombok.Data;

/**
 * @author future0923
 */
@Data
public class TraceMethodDTO {

    /**
     * 是否追踪方法耗时
     */
    private Boolean traceMethod = false;

    /**
     * 追踪方法最大深度
     */
    private Integer traceMaxDepth = 1;

    /**
     * 追踪MyBatis方法
     */
    private Boolean traceMyBatis = false;

    /**
     * 追踪SQL
     */
    private Boolean traceSQL = false;

    /**
     * 跳过getter和setter开头方法
     */
    private Boolean traceSkipStartGetSetCheckBox = false;

    /**
     * 只追踪该业务包方法的前缀
     */
    private String traceBusinessPackageRegexp;

    /**
     * 忽略追踪方法包前缀
     */
    private String traceIgnorePackageRegexp;
}
