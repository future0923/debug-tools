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

import java.lang.management.*;
import java.util.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 保留调用执行线程与终态，超时查询不执行中断或重新调用。 */
public final class InvocationDiagnostics {
    private static final Map<String,Record> RECORDS=new LinkedHashMap<>();
    private InvocationDiagnostics() { }
    public static synchronized void start(String id,String className,String methodName) {
        if(id==null)return;
        long now=System.currentTimeMillis();RECORDS.values().removeIf(record->record.finished>0 && now-record.started>1800000);
        if(RECORDS.size()>=512) {
            Iterator<Record> iterator=RECORDS.values().iterator();while(iterator.hasNext()){if(iterator.next().finished>0){iterator.remove();break;}}
        }
        // 无空位时不干扰业务执行，诊断返回 UNKNOWN 而不是阻塞业务线程。
        if(RECORDS.size()<512) RECORDS.put(id,new Record(id,className,methodName));
    }
    public static synchronized void finish(String id,boolean responseCompleted) {
        Record record=RECORDS.get(id);if(record!=null){record.finished=System.currentTimeMillis();record.state=responseCompleted?"RESPONSE_RECORDED":"METHOD_RETURNED_OR_STREAMING";}
    }
    public static synchronized Map<String,Object> inspect(String id,int maxDepth) {
        if(id==null || maxDepth<1 || maxDepth>128)throw new IllegalArgumentException("invocationId and maxDepth 1..128 required");
        Record record=RECORDS.get(id);
        if(record==null)return map("invocationId",id,"state","UNKNOWN_OR_EXPIRED","mayHaveExecuted",true);
        List<String> stack=new ArrayList<>();String threadState=null;Long lockOwner=null;
        if(record.finished==0) {
            ThreadInfo thread=ManagementFactory.getThreadMXBean().getThreadInfo(record.threadId,maxDepth);
            if(thread!=null){threadState=thread.getThreadState().name();lockOwner=thread.getLockOwnerId();for(StackTraceElement element:thread.getStackTrace())stack.add(element.toString());}
        }
        return map("invocationId",id,"state",record.state,"className",record.className,"methodName",record.methodName,"startedAt",record.started,
            "finishedAt",record.finished,"executionThreadId",record.threadId,"threadState",threadState,"lockOwnerId",lockOwner,"stack",stack,
            "elapsedMillis",(record.finished==0?System.currentTimeMillis():record.finished)-record.started,"cancellationSupported",false);
    }
    private static final class Record {
        final String id,className,methodName;final long threadId=Thread.currentThread().getId(),started=System.currentTimeMillis();long finished;String state="EXECUTING";
        Record(String id,String className,String methodName){this.id=id;this.className=className;this.methodName=methodName;}
    }
}
