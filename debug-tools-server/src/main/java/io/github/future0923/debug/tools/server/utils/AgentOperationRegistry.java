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
import java.util.concurrent.*;
import java.util.function.LongSupplier;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 有界异步写操作记录：同一未过期 ID 只执行一次，超时查询不重新执行。 */
public final class AgentOperationRegistry implements AutoCloseable {
    public static final long RETENTION_MILLIS = 30 * 60 * 1000L;
    private final Map<String, Operation> operations = new LinkedHashMap<>();
    private final ThreadPoolExecutor executor;
    private final LongSupplier clock;
    public AgentOperationRegistry() { this(System::currentTimeMillis); }
    AgentOperationRegistry(LongSupplier clock) {
        this.clock=clock;
        executor=new ThreadPoolExecutor(2,2,0,TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(8), runnable -> {
            Thread thread=new Thread(runnable,"DebugTools-MCP-operation");thread.setDaemon(true);return thread;
        });
    }
    public synchronized Map<String,Object> submit(String id,String fingerprint,Callable<Object> action) {
        validateId(id);
        long now=clock.getAsLong();
        operations.values().removeIf(op -> op.finishedAt > 0 && now-op.issuedAt > RETENTION_MILLIS);
        Operation previous=operations.get(id);
        if(previous!=null) {
            if(!previous.fingerprint.equals(fingerprint)) throw new IllegalArgumentException("Operation ID reused with different input");
            return previous.snapshot();
        }
        if(operations.size()>=64) throw new RejectedExecutionException("Operation history capacity reached; retained IDs are not evicted early");
        Operation operation=new Operation(id,fingerprint,Long.parseLong(id.split("_")[1]));
        operations.put(id,operation);
        try { executor.execute(() -> {
            operation.threadId=Thread.currentThread().getId();operation.startedAt=clock.getAsLong();operation.state="RUNNING";
            try { operation.result=action.call();operation.state=operation.result instanceof Map && "PARTIAL_OR_FAILED".equals(((Map<?,?>)operation.result).get("state")) ? "PARTIAL_OR_FAILED" : "SUCCEEDED"; }
            catch(Throwable error) { operation.error=map("type",error.getClass().getName(),"message",String.valueOf(error.getMessage()).substring(0,Math.min(8192,String.valueOf(error.getMessage()).length())));operation.state="FAILED"; }
            finally { operation.finishedAt=clock.getAsLong(); }
        }); } catch(RejectedExecutionException rejected) { operations.remove(id); throw rejected; }
        return operation.snapshot();
    }
    public synchronized Map<String,Object> get(String id) {
        if(id==null) throw new IllegalArgumentException("operationId is required");
        Operation operation=operations.get(id);
        if(operation==null) return map("operationId",id,"state","UNKNOWN_OR_EXPIRED","mayHaveExecuted",true);
        if(operation.finishedAt > 0 && clock.getAsLong()-operation.issuedAt>RETENTION_MILLIS) {
            operations.remove(id);return map("operationId",id,"state","EXPIRED","mayHaveExecuted",true);
        }
        return operation.snapshot();
    }
    private void validateId(String id) {
        if(id==null || !id.matches("op_[0-9]{13}_[0-9a-f-]{36}")) throw new IllegalArgumentException("operationId must be op_<epochMillis>_<UUID>");
        long age=clock.getAsLong()-Long.parseLong(id.split("_")[1]);
        if(age < -60000 || age>RETENTION_MILLIS) throw new IllegalArgumentException("Operation ID is outside its 30 minute submission window");
    }
    @Override public synchronized void close() { executor.shutdownNow();operations.clear(); }
    private static final class Operation {
        final String id,fingerprint;final long issuedAt;
        volatile String state="QUEUED";volatile long threadId,startedAt,finishedAt;volatile Object result,error;
        Operation(String id,String fingerprint,long issuedAt) { this.id=id;this.fingerprint=fingerprint;this.issuedAt=issuedAt; }
        Map<String,Object> snapshot() { return map("operationId",id,"state",state,"issuedAt",issuedAt,"startedAt",startedAt,"finishedAt",finishedAt,
            "executionThreadId",threadId,"result",result,"error",error,"retentionMillis",RETENTION_MILLIS,"cancellationSupported",false); }
    }
}
