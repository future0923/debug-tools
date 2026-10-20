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

import net.bytebuddy.agent.ByteBuddyAgent;
import io.github.future0923.debug.tools.base.trace.MethodWatchBuffer;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

/** 在专用测试 JVM 上执行真实 retransformation；不连接外部业务应用。 */
class MethodWatchManagerTest {
    public static class Target {
        public int add(int value) { return value+1; }
        public void fail() { throw new IllegalStateException("original failure"); }
    }
    @Test void observesRealCallsAndExceptionsThenResetsInstrumentation() throws Exception {
        java.lang.instrument.Instrumentation instrumentation=ByteBuddyAgent.install();
        Target target=new Target();
        try(MethodWatchManager manager=new MethodWatchManager()) {
            String id=(String)manager.start(instrumentation,Target.class,"add","(I)I",2,10000).get("watchId");
            assertEquals(3,target.add(2));assertEquals(4,target.add(3));assertEquals(5,target.add(4));
            Map<String,Object> result=manager.read(id,0,100);List<?> events=(List<?>)result.get("events");
            assertEquals(2,events.size());assertEquals("3",((Map<?,?>)events.get(0)).get("result"));
            assertEquals("STOPPED",manager.stop(id,false).get("state"));target.add(10);
            assertEquals(2,manager.read(id,0,100).get("observedCalls"));manager.stop(id,true);
            String failureId=(String)manager.start(instrumentation,Target.class,"fail","()V",2,10000).get("watchId");
            IllegalStateException error=assertThrows(IllegalStateException.class,target::fail);assertEquals("original failure",error.getMessage());
            Map<?,?> event=(Map<?,?>)((List<?>)manager.read(failureId,0,100).get("events")).get(0);
            assertEquals("THREW",event.get("state"));
        }
    }
    @Test void completionUpdatesAreVisibleAfterEntryCursorWasConsumed() {
        String id=UUID.randomUUID().toString();MethodWatchBuffer.create(id,2,System.currentTimeMillis()+10000);
        try {
            long call=MethodWatchBuffer.enter(id,new Object[]{"input"});
            long cursor=(Long)MethodWatchBuffer.snapshot(id,0,50).get("nextCursor");
            MethodWatchBuffer.exit(id,call,"done",null);
            List<?> events=(List<?>)MethodWatchBuffer.snapshot(id,cursor,50).get("events");
            assertEquals(1,events.size());assertEquals("RETURNED",((Map<?,?>)events.get(0)).get("state"));
        } finally {MethodWatchBuffer.release(id);}
    }
}
