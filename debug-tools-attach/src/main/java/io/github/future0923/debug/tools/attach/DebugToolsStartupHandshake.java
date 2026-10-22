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
package io.github.future0923.debug.tools.attach;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.http.handler.RuntimeDiagnosticsHttpHandler;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;

/** 为跨系统启动输出有界握手，IDEA 无需读取目标 PID 或共享用户目录。 */
public final class DebugToolsStartupHandshake {
    public static final String STARTUP_ID_PROPERTY = "debug.tools.startup.id";
    private static final Logger logger = Logger.getLogger(DebugToolsStartupHandshake.class);

    private DebugToolsStartupHandshake() { }

    /** 初始化前先绑定运行实例，随后即使启动失败也能由对应 handler 清理。 */
    public static void starting() {
        String id = startupId();
        if (id != null) {
            System.out.println("DEBUG_TOOLS_STARTING_V1:" + id);
            System.out.flush();
        }
    }

    /** 仅发布已绑定的真实端口和 agent 实例，避免端口猜测及并行启动串连。 */
    public static void ready() {
        String id = startupId();
        if (id == null || !DebugToolsBootstrap.started) return;
        try {
            Properties properties = new Properties();
            properties.setProperty("protocolVersion", "1");
            properties.setProperty("startupId", id);
            properties.setProperty("agentInstanceId", RuntimeDiagnosticsHttpHandler.INSTANCE_ID);
            properties.setProperty("pid", ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            properties.setProperty("tcpPort", String.valueOf(DebugToolsBootstrap.serverConfig.getTcpPort()));
            properties.setProperty("httpPort", String.valueOf(DebugToolsBootstrap.serverConfig.getHttpPort()));
            properties.setProperty("applicationName", DebugToolsBootstrap.serverConfig.getApplicationName());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            properties.store(output, null);
            System.out.println("DEBUG_TOOLS_READY_V1:" + Base64.getEncoder().encodeToString(output.toByteArray()));
            System.out.flush();
        } catch (Exception error) {
            // 握手失败不改变业务应用的启动结果，由 IDE 有界等待并报告连接失败。
            logger.warning("Failed to publish DebugTools startup handshake: {}", error.toString());
        }
    }

    /** 非插件管理的启动不输出协议行，拒绝畸形属性污染控制台协议。 */
    private static String startupId() {
        String value = System.getProperty(STARTUP_ID_PROPERTY);
        if (value == null) return null;
        try {
            return UUID.fromString(value).toString().equals(value) ? value : null;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
