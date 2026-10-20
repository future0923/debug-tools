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
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 在指定应用 ClassLoader 下调用可选 Spring 扩展，始终恢复 HTTP 线程 TCCL。 */
public final class SpringDiagnosticsHttpHandler extends BaseHttpHandler<Map<String,Object>, HttpResponse<Object>> {
    public static final String PATH = "/spring/diagnostics";
    public static final SpringDiagnosticsHttpHandler INSTANCE = new SpringDiagnosticsHttpHandler();
    @Override protected HttpResponse<Object> doHandle(Map<String,Object> req, Headers headers) {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            if (req == null || !(req.get("classLoaderIdentity") instanceof String)) throw new IllegalArgumentException("classLoaderIdentity is required");
            ClassLoader loader = AllClassLoaderHttpHandler.getClassLoader((String)req.get("classLoaderIdentity"));
            Thread.currentThread().setContextClassLoader(loader);
            Class<?> extension = DebugToolsEnvUtils.getSpringEnvUtilClass();
            if (extension == null) return HttpResponse.of(503, map("code", "CAPABILITY_UNAVAILABLE", "message", "Spring extension is unavailable in selected loader"));
            return HttpResponse.of(200, extension.getMethod("inspectSpringRuntime", Map.class).invoke(null, req));
        } catch (IllegalArgumentException error) { return HttpResponse.of(400, map("code", "INVALID_ARGUMENT", "message", error.getMessage())); }
        catch (InvocationTargetException error) {
            Throwable cause = error.getCause();
            return HttpResponse.of(cause instanceof IllegalArgumentException ? 400 : 500, map("code", cause instanceof IllegalArgumentException ? "INVALID_ARGUMENT" : "SPRING_DIAGNOSTIC_FAILED", "message", cause.toString()));
        } catch (Exception | LinkageError error) { return HttpResponse.of(503, map("code", "CAPABILITY_UNAVAILABLE", "message", error.toString())); }
        finally { Thread.currentThread().setContextClassLoader(previous); }
    }
}
