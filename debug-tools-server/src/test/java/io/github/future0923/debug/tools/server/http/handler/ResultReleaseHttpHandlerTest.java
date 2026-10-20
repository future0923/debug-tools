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
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultReleaseHttpHandlerTest {
    @Test void reportsActualRemovalAndPreservesOtherRoots() {
        DebugToolsResultUtils.putCache("release-a", new Object());
        DebugToolsResultUtils.putCache("release-b", new Object());
        ResultReleaseHttpHandler.Request req = new ResultReleaseHttpHandler.Request();
        req.offsetPaths = Arrays.asList("release-a", "release-a");
        HttpResponse<Map<String, Object>> response = ResultReleaseHttpHandler.INSTANCE.doHandle(req, new Headers());
        assertEquals(200, response.getStatusCode());
        List<?> rows = (List<?>) response.getBody().get("results");
        assertEquals(true, ((Map<?, ?>) rows.get(0)).get("removed"));
        assertEquals(false, ((Map<?, ?>) rows.get(1)).get("removed"));
        assertNull(DebugToolsResultUtils.getValueByOffset("release-a"));
        assertNotNull(DebugToolsResultUtils.getValueByOffset("release-b"));
        DebugToolsResultUtils.removeCache("release-b");
    }
    @Test void validatesWholeBatchBeforeRemovingAnyRoot() {
        DebugToolsResultUtils.putCache("release-c", new Object());
        ResultReleaseHttpHandler.Request req = new ResultReleaseHttpHandler.Request();
        req.offsetPaths = Arrays.asList("release-c", "release-c/0@array");
        assertEquals(400, ResultReleaseHttpHandler.INSTANCE.doHandle(req, new Headers()).getStatusCode());
        assertNotNull(DebugToolsResultUtils.getValueByOffset("release-c"));
        DebugToolsResultUtils.removeCache("release-c");
    }
}
