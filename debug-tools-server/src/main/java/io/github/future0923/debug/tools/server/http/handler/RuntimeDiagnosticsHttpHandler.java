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
package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics;
import io.github.future0923.debug.tools.server.utils.BusinessLogCapture;
import java.lang.instrument.Instrumentation;
import java.util.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 诊断协议入口独立于业务执行，返回实际能力及明确的不可用状态。 */
public final class RuntimeDiagnosticsHttpHandler extends BaseHttpHandler<RuntimeDiagnosticsHttpHandler.Request, HttpResponse<Map<String, Object>>> {
    public static final String INSTANCE_ID = UUID.randomUUID().toString();
    private final String operation;
    public RuntimeDiagnosticsHttpHandler(String operation) { this.operation = operation; }

    @Override protected HttpResponse<Map<String, Object>> doHandle(Request request, Headers headers) {
        Request req = request == null ? new Request() : request;
        try {
            Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE == null ? null : DebugToolsBootstrap.INSTANCE.getInstrumentation();
            Map<String, Object> result;
            switch (operation) {
                case "capabilities":
                    result = map("agentVersion", ProjectConstants.VERSION, "protocolVersion", 1, "agentInstanceId", INSTANCE_ID,
                        "processId", java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0], "javaVersion", System.getProperty("java.version"), "instrumentationAvailable", instrumentation != null,
                        "capabilities", map("result.inspect", true, "result.recover", true, "stream.cancel", true,
                            "spring.diagnostics.endpoint", true, "spring.config", true, "spring.ready", true, "runtime.jvm", true, "runtime.threads", true,
                            "runtime.classes", instrumentation != null, "logs.agent", true, "logs.business.capture", true,
                            "logs.business", BusinessLogCapture.status().stream().anyMatch(it -> Boolean.TRUE.equals(it.get("active"))),
                            "trace.selection", instrumentation != null, "runtime.watch", instrumentation != null && instrumentation.isRetransformClassesSupported(), "invocation.targetBinding", true, "invocation.diagnostics", true, "evidence.threadCorrelation", true, "evidence.automaticAsyncPropagation", false, "evidence.selectedExecutorPropagation", instrumentation != null && instrumentation.isRetransformClassesSupported(), "sql.recent", true, "sql.diagnostics", true, "result.release", true, "operations.groovy", true, "operations.resources", true, "operations.compile", true, "operations.classes", instrumentation != null),
                        "limits", map("resultNodes", 2000, "threadPage", 256, "threadDepth", 128, "classPage", 100),
                        "logSources", logSources(), "businessLogCapture", BusinessLogCapture.status());
                    break;
                case "invocation": result = io.github.future0923.debug.tools.server.utils.InvocationDiagnostics.inspect(req.invocationId, req.maxDepth); break;
                case "jvm": result = RuntimeDiagnostics.jvm(); break;
                case "threads": result = RuntimeDiagnostics.threads(req.offset, req.limit, req.maxDepth); break;
                case "classes":
                    if (instrumentation == null) return HttpResponse.of(503, map("code", "CAPABILITY_UNAVAILABLE", "message", "Instrumentation is unavailable"));
                    result = RuntimeDiagnostics.classes(instrumentation.getAllLoadedClasses(), req.className, req.nameContains, req.classLoaderIdentity,
                        req.offset, req.limit, req.methodLimit);
                    break;
                default: throw new IllegalArgumentException("Unknown diagnostic operation");
            }
            return HttpResponse.of(200, result);
        } catch (IllegalArgumentException error) { return HttpResponse.of(400, map("code", "INVALID_ARGUMENT", "message", error.getMessage())); }
        catch (RuntimeException error) { return HttpResponse.of(500, map("code", "DIAGNOSTIC_FAILED", "message", error.toString())); }
    }
    /** 返回当前确实处于 attached 状态的业务日志来源。 */
    private static java.util.List<String> logSources() {
        java.util.List<String> sources = new java.util.ArrayList<>(); sources.add("DEBUG_TOOLS");
        for (Map<String,Object> capture : BusinessLogCapture.status()) {
            if (Boolean.TRUE.equals(capture.get("active"))) sources.add(String.valueOf(capture.get("framework")));
        }
        return sources;
    }
    public static final class Request {
        public String invocationId;
        public int offset;
        public int limit = 50;
        public int maxDepth = 32;
        public int methodLimit = 50;
        public String className;
        public String nameContains;
        public String classLoaderIdentity;
    }
}
