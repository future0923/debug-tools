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
import io.github.future0923.debug.tools.sql.SqlPrintInterceptor;
import io.github.future0923.debug.tools.sql.SqlRecentBuffer;
import io.github.future0923.debug.tools.server.utils.SqlDiagnostics;
import java.util.Arrays;
import java.util.Map;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** JSON SQL 管理协议，与旧版原始字符串端点共存。 */
public final class SqlDiagnosticsHttpHandler extends BaseHttpHandler<SqlDiagnosticsHttpHandler.Request, HttpResponse<Map<String, Object>>> {
    public static final String PATH = "/sql/diagnostics";
    public static final SqlDiagnosticsHttpHandler INSTANCE = new SqlDiagnosticsHttpHandler();
    @Override protected HttpResponse<Map<String, Object>> doHandle(Request req, Headers headers) {
        if (req == null) req = new Request();
        try {
            if ("AGGREGATE".equals(req.action)) return HttpResponse.of(200, SqlDiagnostics.aggregate(SqlRecentBuffer.snapshot(), req.since, req.limit, req.sortBy));
            if (!Arrays.asList("GET_MODE", "SET_MODE").contains(req.action)) throw new IllegalArgumentException("Invalid SQL action");
            // 变更与读回在同一临界区完成，返回旧值和实际新值。
            synchronized (SqlPrintInterceptor.class) {
                String previous = SqlPrintInterceptor.printSqlType.getType();
                if ("SET_MODE".equals(req.action)) {
                    if (!Arrays.asList("Pretty", "Compress", "No").contains(req.mode)) throw new IllegalArgumentException("mode must be Pretty, Compress or No");
                    SqlPrintInterceptor.setPrintSqlType(req.mode);
                }
                return HttpResponse.of(200, map("previousMode", previous, "mode", SqlPrintInterceptor.printSqlType.getType(),
                    "scope", "AGENT", "captureNote", "Recent SQL capture depends on JDBC instrumentation and SQL printing being enabled"));
            }
        } catch (IllegalArgumentException error) { return HttpResponse.of(400, map("code", "INVALID_ARGUMENT", "message", error.getMessage())); }
    }
    public static final class Request {
        public String action = "GET_MODE"; public String mode; public long since; public int limit = 20; public String sortBy = "TOTAL";
    }
}
