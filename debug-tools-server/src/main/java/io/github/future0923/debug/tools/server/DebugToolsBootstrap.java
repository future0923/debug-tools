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
package io.github.future0923.debug.tools.server;

import io.github.future0923.debug.tools.base.config.AgentArgs;
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
        int tcpPort = agentArgs.getTcpPort() == null ? DebugToolsIOUtils.getAvailablePort(12345) : Integer.parseInt(agentArgs.getTcpPort());
        int httpPort = agentArgs.getHttpPort() == null ? DebugToolsIOUtils.getAvailablePort(22222) : Integer.parseInt(agentArgs.getHttpPort());
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
