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
package io.github.future0923.debug.tools.server.http;

import com.sun.net.httpserver.HttpServer;
import io.github.future0923.debug.tools.server.http.handler.GroovyDebugHttpHandler;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.ChangePrintSqlTypeHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.ClassLoaderHasClassHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.GetApplicationNameHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.GetPrintSqlTypeHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.IndexHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RunResultDetailHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RunResultTraceHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RunResultTypeHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.SpringConfigHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.SpringReadyHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RecentLogsHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RecentSqlHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.ResultInspectionHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.RuntimeDiagnosticsHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.BusinessLogCaptureHttpHandler;
import io.github.future0923.debug.tools.server.utils.BusinessLogCapture;
import io.github.future0923.debug.tools.server.http.handler.ResultReleaseHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.SqlDiagnosticsHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.SpringDiagnosticsHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.AgentOperationsHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.MethodWatchHttpHandler;
import io.github.future0923.debug.tools.server.http.handler.TraceSelectionHttpHandler;
import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author future0923
 */
public class DebugToolsHttpServer {

    private static final Logger logger = Logger.getLogger(DebugToolsHttpServer.class);

    private static volatile DebugToolsHttpServer debugToolsHttpServer;

    private HttpServer httpServer;

    private final io.github.future0923.debug.tools.server.http.handler.AsyncContextHttpHandler asyncContextHandler = new io.github.future0923.debug.tools.server.http.handler.AsyncContextHttpHandler();

    private final MethodWatchHttpHandler watchHandler = new MethodWatchHttpHandler();

    private final AgentOperationsHttpHandler operationsHandler = new AgentOperationsHttpHandler();

    @Getter
    private final int port;

    private volatile boolean started = false;

    public DebugToolsHttpServer(int port) {
        int boundPort = port;
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            boundPort = httpServer.getAddress().getPort();
        } catch (IOException e) {
            logger.error("start http server bind port in {} error", e, port);
        }
        this.port = boundPort;
        if (httpServer == null) return;
        httpServer.createContext(IndexHttpHandler.PATH, IndexHttpHandler.INSTANCE);
        httpServer.createContext(GroovyDebugHttpHandler.PATH, GroovyDebugHttpHandler.INSTANCE);
        httpServer.createContext(RunResultTypeHttpHandler.PATH, RunResultTypeHttpHandler.INSTANCE);
        httpServer.createContext(ResultInspectionHttpHandler.PATH, ResultInspectionHttpHandler.INSTANCE);
        httpServer.createContext(RunResultDetailHttpHandler.PATH, RunResultDetailHttpHandler.INSTANCE);
        httpServer.createContext(AllClassLoaderHttpHandler.PATH, AllClassLoaderHttpHandler.INSTANCE);
        httpServer.createContext(ClassLoaderHasClassHttpHandler.PATH, ClassLoaderHasClassHttpHandler.INSTANCE);
        httpServer.createContext(GetApplicationNameHttpHandler.PATH, GetApplicationNameHttpHandler.INSTANCE);
        httpServer.createContext(RunResultTraceHttpHandler.PATH, RunResultTraceHttpHandler.INSTANCE);
        httpServer.createContext(GetPrintSqlTypeHttpHandler.PATH, GetPrintSqlTypeHttpHandler.INSTANCE);
        httpServer.createContext(ChangePrintSqlTypeHttpHandler.PATH, ChangePrintSqlTypeHttpHandler.INSTANCE);
        httpServer.createContext(SpringConfigHttpHandler.PATH, SpringConfigHttpHandler.INSTANCE);
        httpServer.createContext(SpringReadyHttpHandler.PATH, SpringReadyHttpHandler.INSTANCE);
        httpServer.createContext(RecentLogsHttpHandler.PATH, RecentLogsHttpHandler.INSTANCE);
        httpServer.createContext(BusinessLogCaptureHttpHandler.PATH, BusinessLogCaptureHttpHandler.INSTANCE);
        httpServer.createContext(RecentSqlHttpHandler.PATH, RecentSqlHttpHandler.INSTANCE);
        httpServer.createContext(ResultReleaseHttpHandler.PATH, ResultReleaseHttpHandler.INSTANCE);
        httpServer.createContext(SqlDiagnosticsHttpHandler.PATH, SqlDiagnosticsHttpHandler.INSTANCE);
        httpServer.createContext(SpringDiagnosticsHttpHandler.PATH, SpringDiagnosticsHttpHandler.INSTANCE);
        httpServer.createContext(TraceSelectionHttpHandler.PATH, TraceSelectionHttpHandler.INSTANCE);
        httpServer.createContext(MethodWatchHttpHandler.PATH, watchHandler);
        httpServer.createContext(io.github.future0923.debug.tools.server.http.handler.AsyncContextHttpHandler.PATH, asyncContextHandler);
        httpServer.createContext(AgentOperationsHttpHandler.PATH, operationsHandler);
        httpServer.createContext("/capabilities", new RuntimeDiagnosticsHttpHandler("capabilities"));
        httpServer.createContext("/runtime/invocation", new RuntimeDiagnosticsHttpHandler("invocation"));
        httpServer.createContext("/runtime/jvm", new RuntimeDiagnosticsHttpHandler("jvm"));
        httpServer.createContext("/runtime/threads", new RuntimeDiagnosticsHttpHandler("threads"));
        httpServer.createContext("/runtime/classes", new RuntimeDiagnosticsHttpHandler("classes"));
    }

    public void start() {
        if (!started && httpServer != null) {
            httpServer.start();
            started = true;
            logger.info("start http server trans and bind port in {}", port);
        }
    }

    public void close() {
        BusinessLogCapture.closeAll();
        operationsHandler.close();
        watchHandler.close();
        asyncContextHandler.close();
        if (httpServer != null) {
            httpServer.stop(0);
            started = false;
        }
    }

}
