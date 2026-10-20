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
import io.github.future0923.debug.tools.base.trace.InvocationContext;
import io.github.future0923.debug.tools.base.logging.DebugToolsLogBuffer;
import io.github.future0923.debug.tools.sql.SqlRecentBuffer;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class InvocationDiagnosticsTest {
    @Test void explicitAsyncWrapperPropagatesSubmitContextAndDoesNotLeakIntoNextTask() throws Exception {
        ExecutorService executor=Executors.newSingleThreadExecutor();
        try {
            try(InvocationContext.Scope scope=InvocationContext.open("call-async")) {
                assertEquals("call-async",executor.submit(InvocationContext.wrap((Callable<String>)InvocationContext::currentId)).get(3,TimeUnit.SECONDS));
            }
            assertNull(executor.submit(InvocationContext::currentId).get(3,TimeUnit.SECONDS));assertNull(InvocationContext.currentId());
        } finally { executor.shutdownNow(); }
    }
    @Test void liveThreadStatusAndSqlAgentLogFilteringUseExactInvocationId() {
        String id="diagnostics-"+UUID.randomUUID();
        InvocationDiagnostics.start(id,"example.Service","run");
        Map<String,Object> running=InvocationDiagnostics.inspect(id,5);
        assertEquals("EXECUTING",running.get("state"));assertEquals(Thread.currentThread().getId(),running.get("executionThreadId"));assertFalse(((List<?>)running.get("stack")).isEmpty());
        try(InvocationContext.Scope scope=InvocationContext.open(id)) {
            DebugToolsLogBuffer.add("INFO","test",Thread.currentThread().getName(),"correlated",null);
            SqlRecentBuffer.add("select 42",4,"mysql","test");
        }
        assertEquals(1,DebugToolsLogBuffer.recent(100,0,null,null,id).size());assertEquals(1,SqlRecentBuffer.recent(100,0,null,id).size());
        assertTrue(SqlRecentBuffer.recent(100,0,null,"unrelated").isEmpty());
        InvocationDiagnostics.finish(id,true);assertEquals("RESPONSE_RECORDED",InvocationDiagnostics.inspect(id,5).get("state"));
    }
    @Test void businessLogMdcCarriesInvocationIdentityAndRestoresExistingValue() throws Exception {
        String id="business-"+UUID.randomUUID();ClassLoader loader=getClass().getClassLoader();
        org.slf4j.MDC.put(InvocationLogContext.KEY,"previous");
        BusinessLogCapture.start("correlation-test",loader,"LOGBACK");
        try {
            try(InvocationLogContext scope=InvocationLogContext.open(loader,id)) { org.slf4j.LoggerFactory.getLogger("correlation.test").info("captured business log"); }
            assertEquals("previous",org.slf4j.MDC.get(InvocationLogContext.KEY));
            assertFalse(DebugToolsLogBuffer.recent(100,0,null,null,id).isEmpty());
        } finally { BusinessLogCapture.stop("correlation-test","LOGBACK");org.slf4j.MDC.remove(InvocationLogContext.KEY); }
    }
}
