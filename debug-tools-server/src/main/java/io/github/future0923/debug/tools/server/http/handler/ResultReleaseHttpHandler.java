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
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 定向释放结果和 trace 根缓存，每个条目返回实际移除证据。 */
public final class ResultReleaseHttpHandler extends BaseHttpHandler<ResultReleaseHttpHandler.Request, HttpResponse<Map<String, Object>>> {
    public static final String PATH = "/result/release";
    public static final ResultReleaseHttpHandler INSTANCE = new ResultReleaseHttpHandler();
    @Override protected HttpResponse<Map<String, Object>> doHandle(Request req, Headers headers) {
        if (req == null || req.offsetPaths == null || req.offsetPaths.isEmpty() || req.offsetPaths.size() > 100)
            return HttpResponse.of(400, map("code", "INVALID_ARGUMENT", "message", "Provide 1..100 root offsetPaths"));
        // 全量校验后才修改缓存，避免参数错误产生部分删除。
        for (String path : req.offsetPaths) if (path == null || path.trim().isEmpty() || path.length() > 512 || path.contains("/"))
            return HttpResponse.of(400, map("code", "INVALID_ARGUMENT", "message", "Only root offsetPaths may be released"));
        List<Map<String, Object>> results = new ArrayList<>();
        for (String path : req.offsetPaths) results.add(map("offsetPath", path, "removed", DebugToolsResultUtils.releaseRoot(path)));
        return HttpResponse.of(200, map("results", results, "scope", "RESULT_ROOTS", "invocationResponseRetained", true));
    }
    public static final class Request { public List<String> offsetPaths; }
}
