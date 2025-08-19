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

import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
@Data
public class RunDTO {

    /**
     * 请求头信息
     */
    private Map<String, String> headers;

    /**
     * 使用的类加载器
     */
    private AllClassLoaderRes.Item classLoader;

    /**
     * 调用的类名
     */
    private String targetClassName;

    /**
     * 调用的方法
     */
    private String targetMethodName;

    /**
     * 调用的方法参数
     */
    private List<String> targetMethodParameterTypes;

    /**
     * 调用的方法入参
     */
    private Map<String, RunContentDTO> targetMethodContent;

    /**
     * xxljob参数
     */
    private String xxlJobParam;

    /**
     * 追踪方法
     */
    private TraceMethodDTO traceMethodDTO;

    /**
     * 调用方法around
     */
    private String methodAroundContent;

}