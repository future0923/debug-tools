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
package io.github.future0923.debug.tools.server;

import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsIOUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsJvmUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.server.config.ServerConfig;
import io.github.future0923.debug.tools.server.http.DebugToolsHttpServer;
import io.github.future0923.debug.tools.server.scoket.DebugToolsSocketServer;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import io.github.future0923.debug.tools.vm.JvmToolsUtils;
import lombok.Getter;

import java.lang.instrument.Instrumentation;

/**
 * @author future0923
 */
public class DebugToolsBootstrap {

    private static final Logger logger = Logger.getLogger(DebugToolsBootstrap.class);

    public static volatile DebugToolsBootstrap INSTANCE;

    public static volatile boolean started = false;

    private DebugToolsSocketServer socketServer;

    private DebugToolsHttpServer httpServer;

    @Getter
    private final Instrumentation instrumentation;

    public static final ServerConfig serverConfig = new ServerConfig();

    private Integer tcpPort;

    public Integer httpPort;

    private DebugToolsBootstrap(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        JvmToolsUtils.init();
    }

    public static synchronized DebugToolsBootstrap getInstance(Instrumentation instrumentation) {
        if (INSTANCE == null) {
            INSTANCE = new DebugToolsBootstrap(instrumentation);
        }
        return INSTANCE;
    }

    public void start(AgentArgs agentArgs) {
        int tcpPort = StrUtil.isBlank(agentArgs.getTcpPort()) ? DebugToolsIOUtils.getAvailablePort(12345) : Integer.parseInt(agentArgs.getTcpPort());
        int httpPort = StrUtil.isBlank(agentArgs.getHttpPort()) ? DebugToolsIOUtils.getAvailablePort(22222) : Integer.parseInt(agentArgs.getHttpPort());
        serverConfig.setApplicationName(getApplicationName(agentArgs));
        serverConfig.setTcpPort(tcpPort);
        serverConfig.setHttpPort(httpPort);
        startTcpServer(tcpPort);
        startHttpServer(httpPort);
        started = true;
    }

    private String getApplicationName(AgentArgs parse) {
        if (DebugToolsStringUtils.isNotBlank(parse.getApplicationName())) {
            return parse.getApplicationName();
        }
        try {
            String applicationName = (String) DebugToolsEnvUtils.getSpringConfig("spring.application.name");
            if (applicationName != null) {
                return applicationName;
            }
        } catch (Exception ignored) {
        }
        return DebugToolsJvmUtils.getApplicationName();
    }

    private void startTcpServer(int tcpPort) {
        if (!started || socketServer == null) {
            socketServer = new DebugToolsSocketServer();
            socketServer.start();
        } else if (this.tcpPort != null && tcpPort != this.tcpPort) {
            logger.error("The tcp two ports are inconsistent. Stopping port {}, preparing to start port {}", this.tcpPort, tcpPort);
            socketServer.close();
            socketServer = new DebugToolsSocketServer();
            socketServer.start();
        }
        this.tcpPort = tcpPort;
    }

    private void startHttpServer(int httpPort) {
        if (!started || httpServer == null) {
            httpServer = new DebugToolsHttpServer(httpPort);
            httpServer.start();
        } else if (this.httpPort != null && httpPort != this.httpPort) {
            logger.error("The http two ports are inconsistent. Stopping port {}, preparing to start port {}", this.httpPort, httpPort);
            httpServer.close();
            httpServer = new DebugToolsHttpServer(httpPort);
            httpServer.start();
        }
        this.httpPort = httpPort;
    }

    public void stop() {
        if (socketServer != null) {
            socketServer.close();
            socketServer = null;
        }
        if(httpServer != null) {
            httpServer.close();
            httpServer = null;
        }
        started = false;
        logger.info("stop successful");
    }
}
