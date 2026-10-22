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
     * 运行唯一标识
     */
    private String identity;

    /** 可选 agent 实例约束；新版 MCP 防止重连到重启 JVM 后误执行。 */
    private String expectedAgentInstanceId;

    /**
     * 请求头信息
     */
    private Map<String, String> headers;

    /** 无 Header 时仍初始化模拟 Web 请求上下文；默认沿用有 Header 才初始化的行为。 */
    private boolean initializeWebContext;

    /** 统一插件调用与扩展模块的上下文初始化条件。 */
    public boolean shouldInitializeWebContext() {
        return initializeWebContext || (headers != null && !headers.isEmpty());
    }

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

    /**
     * 调用方法around内容唯一标识(用来判断是否需要重新编译)
     */
    private String methodAroundContentIdentity;

}
