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
package io.github.future0923.debug.tools.server.trace;
import io.github.future0923.debug.tools.base.trace.InvocationContext;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** 真实重转换验证提交时捕获、线程恢复、异常和撤销，不连接业务 JVM。 */
class AsyncContextManagerTest {
    public static class ApplicationExecutor implements Executor {final ExecutorService pool=Executors.newSingleThreadExecutor();public void execute(Runnable task){pool.execute(task);}}
    @Test void selectedExecutorPropagatesContextAndResetStopsFutureCapture() throws Exception {
        ApplicationExecutor executor=new ApplicationExecutor();
        try(AsyncContextManager manager=new AsyncContextManager()) {
            String id=(String)manager.start(ByteBuddyAgent.install(),ApplicationExecutor.class).get("contextId");
            CompletableFuture<String> observed=new CompletableFuture<>();
            try(InvocationContext.Scope scope=InvocationContext.open("async-call")){executor.execute(()->observed.complete(InvocationContext.currentId()));}
            assertEquals("async-call",observed.get(3,TimeUnit.SECONDS));
            assertNull(executor.pool.submit(InvocationContext::currentId).get(3,TimeUnit.SECONDS));
            assertEquals("STOPPED",manager.stop(id).get("state"));
            CompletableFuture<String> after=new CompletableFuture<>();
            try(InvocationContext.Scope scope=InvocationContext.open("later")){executor.execute(()->after.complete(InvocationContext.currentId()));}
            assertNull(after.get(3,TimeUnit.SECONDS));
        }finally{executor.pool.shutdownNow();}
    }
}
