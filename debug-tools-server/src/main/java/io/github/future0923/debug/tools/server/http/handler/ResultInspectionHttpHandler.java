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
import io.github.future0923.debug.tools.server.utils.ResultInspector;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/** 在目标 JVM 内分页、限深和检查结果缓存失效。 */
public final class ResultInspectionHttpHandler extends BaseHttpHandler<ResultInspectionHttpHandler.Request, HttpResponse<Map<String, Object>>> {
    public static final String PATH = "/result/inspect";
    public static final ResultInspectionHttpHandler INSTANCE = new ResultInspectionHttpHandler();
    private ResultInspectionHttpHandler() { }
    @Override
    protected HttpResponse<Map<String, Object>> doHandle(Request request, Headers headers) {
        try {
            if (request == null) throw new IllegalArgumentException("Request is required");
            return HttpResponse.of(200, ResultInspector.inspect(request.offsetPath, request.view, request.offset, request.limit, request.maxDepth, request.maxNodes));
        } catch (NoSuchElementException error) { return failure(410, "RESULT_EXPIRED", error.getMessage()); }
        catch (IllegalArgumentException error) { return failure(400, "INVALID_ARGUMENT", error.getMessage()); }
    }
    private HttpResponse<Map<String, Object>> failure(int status, String code, String message) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", code); result.put("message", message);
        return HttpResponse.of(status, result);
    }
    public static final class Request {
        public String offsetPath;
        public String view = "DEBUG";
        public int offset;
        public int limit = 50;
        public int maxDepth = 5;
        public int maxNodes = 1000;
    }
}
