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
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class AgentOperationRegistryTest {
    @Test void concurrentResubmissionDoesNotExecuteTwiceAndResultCanBeRecovered() throws Exception {
        AtomicInteger calls=new AtomicInteger();CountDownLatch started=new CountDownLatch(1),release=new CountDownLatch(1);
        String id="op_"+System.currentTimeMillis()+"_"+UUID.randomUUID();
        try(AgentOperationRegistry registry=new AgentOperationRegistry()) {
            registry.submit(id,"same",()->{calls.incrementAndGet();started.countDown();release.await();return "answer";});
            assertTrue(started.await(3,TimeUnit.SECONDS));
            Map<String,Object> retry=registry.submit(id,"same",()->{calls.incrementAndGet();return "wrong";});
            assertEquals("RUNNING",retry.get("state"));
            assertThrows(IllegalArgumentException.class,()->registry.submit(id,"different",()->"bad"));
            release.countDown();
            long deadline=System.nanoTime()+TimeUnit.SECONDS.toNanos(3);
            while(!"SUCCEEDED".equals(registry.get(id).get("state")) && System.nanoTime()<deadline) Thread.yield();
            assertEquals("answer",registry.get(id).get("result"));assertEquals(1,calls.get());
        } finally { release.countDown(); }
    }
    @Test void expiredOperationIdsCannotBeResubmittedAfterHistoryRemoval() throws Exception {
        AtomicLong clock=new AtomicLong(System.currentTimeMillis());
        String id="op_"+clock.get()+"_"+UUID.randomUUID();
        try(AgentOperationRegistry registry=new AgentOperationRegistry(clock::get)) {
            registry.submit(id,"hash",()->null);
            clock.addAndGet(AgentOperationRegistry.RETENTION_MILLIS+1);
            assertThrows(IllegalArgumentException.class,()->registry.submit(id,"hash",()->null));
        }
    }
}
