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
package io.github.future0923.debug.tools.server.utils;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 用真实 JVM 线程及类元数据验证预算与只读快照。 */
class RuntimeDiagnosticsTest {
    @Test void capturesBlockedThreadStackWithoutStoppingThread() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread worker = new Thread(() -> { started.countDown(); try { release.await(); } catch (InterruptedException ignored) { Thread.currentThread().interrupt(); } }, "diagnostic-test-worker");
        worker.start();
        try {
            assertTrue(started.await(2, TimeUnit.SECONDS));
            Map<String, Object> result = RuntimeDiagnostics.threads(0, 256, 4);
            List<?> items = (List<?>) result.get("items");
            Map<?, ?> selected = (Map<?, ?>) items.stream().filter(item -> "diagnostic-test-worker".equals(((Map<?, ?>) item).get("name"))).findFirst().get();
            assertEquals(worker.getId(), selected.get("id"));
            assertTrue(((List<?>) selected.get("stack")).size() <= 4);
            assertTrue(worker.isAlive());
            assertThrows(IllegalArgumentException.class, () -> RuntimeDiagnostics.threads(0, 257, 4));
        } finally { release.countDown(); worker.join(2000); }
    }
    @Test void loadedClassMetadataIsFilteredPagedAndBounded() {
        Class<?>[] loaded = {String.class, Integer.class, Long.class};
        Map<String, Object> result = RuntimeDiagnostics.classes(loaded, null, "java.lang", "bootstrap", 1, 1, 2);
        assertEquals(3, result.get("count"));
        assertEquals(2, result.get("nextOffset"));
        Map<?, ?> item = (Map<?, ?>) ((List<?>) result.get("items")).get(0);
        assertEquals("java.lang.Integer", item.get("className"));
        assertEquals(2, ((List<?>) item.get("methods")).size());
        assertEquals(true, item.get("methodsTruncated"));
        assertEquals(0, RuntimeDiagnostics.classes(loaded, "missing.Type", null, null, 0, 1, 0).get("count"));
    }
    @Test void jvmSnapshotReportsRealCounters() {
        Map<String, Object> result = RuntimeDiagnostics.jvm();
        assertTrue(((Number) result.get("uptimeMillis")).longValue() >= 0);
        assertTrue(((Number) ((Map<?, ?>) result.get("heap")).get("used")).longValue() > 0);
        assertTrue(((Number) result.get("threads")).intValue() > 0);
    }
}
