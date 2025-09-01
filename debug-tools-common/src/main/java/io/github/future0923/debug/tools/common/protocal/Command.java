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
package io.github.future0923.debug.tools.common.protocal;

/**
 * @author future0923
 */
public interface Command {

    /**
     * 心跳请求
     */
    Byte HEARTBEAT_REQUEST = 1;

    /**
     * 心跳响应
     */
    Byte HEARTBEAT_RESPONSE = 2;

    /**
     * 运行目标方法请求
     */
    Byte RUN_TARGET_METHOD_REQUEST = 3;

    /**
     * 运行目标方法响应
     */
    Byte RUN_TARGET_METHOD_RESPONSE = 4;

    /**
     * 服务器关闭请求
     */
    Byte SERVER_CLOSE_REQUEST = 5;

    /**
     * 清空运行结果请求
     */
    Byte CLEAR_RUN_RESULT = 7;

    /**
     * 运行Groovy脚本请求
     */
    Byte RUN_GROOVY_SCRIPT_REQUEST = 8;

    /**
     * 运行Groovy脚本响应
     */
    Byte RUN_GROOVY_SCRIPT_RESPONSE = 9;

    /**
     * 本地编译器HotDeploy请求
     */
    Byte LOCAL_COMPILER_HOT_DEPLOY_REQUEST = 10;

    /**
     * 远程编译器HotDeploy请求
     */
    Byte REMOTE_COMPILER_HOT_DEPLOY_REQUEST = 11;

    /**
     * HotDeploy响应
     */
    Byte REMOTE_COMPILER_HOT_DEPLOY_RESPONSE = 12;

    /**
     * 修改追踪方法请求
     */
    Byte CHANGE_TRACE_METHOD_REQUEST = 13;

    /**
     * 资源HotDeploy请求
     */
    Byte RESOURCE_HOT_DEPLOY_REQUEST = 14;
}
