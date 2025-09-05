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
package io.github.future0923.debug.tools.idea.tool.ui;

import io.github.future0923.debug.tools.common.dto.RunDTO;
import lombok.Data;

/**
 * @author future0923
 */
@Data
public class InvokeMethodRecordDTO {

    private String identity;

    /**
     * 全类名
     */
    private String className;

    /**
     * 简类名
     */
    private String classSimpleName;

    /**
     * 方法名
     */
    private String methodName;

    private String methodSignature;

    /**
     * 方法参数
     */
    private String methodParamJson;

    private String methodAroundName;

    private String cacheKey;

    /**
     * 调用参数
     */
    private RunDTO runDTO;
}
