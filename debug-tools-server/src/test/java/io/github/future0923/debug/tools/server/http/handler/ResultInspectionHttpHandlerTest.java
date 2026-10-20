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

import com.sun.net.httpserver.HttpServer;
import io.github.future0923.debug.tools.base.utils.DebugToolsIOUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;
import io.github.future0923.debug.tools.server.utils.ResultInspector;
import org.junit.jupiter.api.Test;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** 真实缓存与 HTTP 回包覆盖分页、空值、过期、无效偏移和循环对象。 */
class ResultInspectionHttpHandlerTest {
    @Test void primitiveArrayIsPagedWithoutCastingToObjectArray() {
        DebugToolsResultUtils.putCache("array-test", new int[]{10, 20, 30, 40});
        try {
            Map<String, Object> page = ResultInspector.inspect("array-test", "CHILDREN", 1, 2, 5, 100);
            List<?> items = (List<?>) page.get("items");
            assertEquals(2, items.size());
            assertEquals("20", ((Map<?, ?>) items.get(0)).get("value"));
            assertEquals(3, page.get("nextOffset"));
            Map<String, Object> selected = ResultInspector.inspect("array-test/1@collection", "JSON", 0, 50, 5, 100);
            assertEquals(20, selected.get("value"));
        } finally { DebugToolsResultUtils.removeCache("array-test"); }
    }
    @Test void largeCollectionOnlyVisitsRequestedPage() {
        final int[] reads = {0};
        List<Integer> lazy = new AbstractList<Integer>() {
            public int size() { return 1000000; }
            public Integer get(int index) { reads[0]++; return index; }
        };
        DebugToolsResultUtils.putCache("large-test", lazy);
        try {
            Map<String, Object> result = ResultInspector.inspect("large-test", "CHILDREN", 0, 3, 5, 100);
            assertEquals(3, ((List<?>) result.get("items")).size());
            assertEquals(4, reads[0]);
        } finally { DebugToolsResultUtils.removeCache("large-test"); }
    }
    @Test void cyclicJsonIsBoundedAndNullChildIsNotExpired() {
        List<Object> value = new ArrayList<>(); value.add(null); value.add(value);
        DebugToolsResultUtils.putCache("cycle-test", value);
        try {
            assertNull(ResultInspector.inspect("cycle-test/0@collection", "JSON", 0, 10, 5, 100).get("value"));
            List<?> snapshot = (List<?>) ResultInspector.inspect("cycle-test", "JSON", 0, 10, 5, 100).get("value");
            assertEquals("cycle-test", ((Map<?, ?>) snapshot.get(1)).get("$ref"));
            assertThrows(IllegalArgumentException.class, () -> ResultInspector.inspect("cycle-test/999999@object", "DEBUG", 0, 10, 5, 100));
        } finally { DebugToolsResultUtils.removeCache("cycle-test"); }
    }
    @Test void httpReportsReleasedResultAsGone() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext(ResultInspectionHttpHandler.PATH, ResultInspectionHttpHandler.INSTANCE);
        server.start();
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL("http://127.0.0.1:" + server.getAddress().getPort() + ResultInspectionHttpHandler.PATH).openConnection();
            connection.setRequestMethod("POST"); connection.setDoOutput(true);
            connection.getOutputStream().write("{\"offsetPath\":\"missing-result\"}".getBytes(StandardCharsets.UTF_8));
            assertEquals(410, connection.getResponseCode());
            Map<?, ?> body = DebugToolsJsonUtils.toBean(new String(DebugToolsIOUtils.readAllBytes(connection.getErrorStream()), StandardCharsets.UTF_8), Map.class);
            assertEquals("RESULT_EXPIRED", body.get("code"));
        } finally { if (connection != null) connection.disconnect(); server.stop(0); }
    }
}
