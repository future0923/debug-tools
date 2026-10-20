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
import io.github.future0923.debug.tools.server.utils.BusinessLogCapture;
import java.util.Map;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 显式管理业务日志采集；读取已有日志不会偷偷改变日志框架。 */
public final class BusinessLogCaptureHttpHandler extends BaseHttpHandler<BusinessLogCaptureHttpHandler.Request, HttpResponse<Map<String, Object>>> {
    public static final String PATH = "/logs/capture";
    public static final BusinessLogCaptureHttpHandler INSTANCE = new BusinessLogCaptureHttpHandler();
    private BusinessLogCaptureHttpHandler() { }

    @Override protected HttpResponse<Map<String, Object>> doHandle(Request request, Headers headers) {
        Request req = request == null ? new Request() : request;
        try {
            switch (req.action) {
                case "STATUS": return HttpResponse.of(200, map("sessions", BusinessLogCapture.status(), "agentLogsAvailable", true));
                case "START": case "STOP":
                    if (req.classLoaderIdentity == null || req.classLoaderIdentity.isEmpty()) throw new IllegalArgumentException("classLoaderIdentity is required");
                    if (!"LOGBACK".equals(req.framework) && !"LOG4J2".equals(req.framework)) throw new IllegalArgumentException("framework must be LOGBACK or LOG4J2");
                    return HttpResponse.of(200, "START".equals(req.action)
                        ? BusinessLogCapture.start(req.classLoaderIdentity, AllClassLoaderHttpHandler.getClassLoader(req.classLoaderIdentity), req.framework)
                        : BusinessLogCapture.stop(req.classLoaderIdentity, req.framework));
                default: throw new IllegalArgumentException("action must be STATUS, START or STOP");
            }
        } catch (IllegalArgumentException error) { return HttpResponse.of(400, map("code", "INVALID_ARGUMENT", "message", error.getMessage())); }
        catch (Exception | LinkageError error) { return HttpResponse.of(503, map("code", "LOG_CAPTURE_UNAVAILABLE", "message", error.toString())); }
    }
    public static final class Request {
        public String action = "STATUS";
        public String classLoaderIdentity;
        public String framework;
    }
}
