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
package io.github.future0923.debug.tools.server.netty.handler;

import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import io.netty.channel.ChannelHandlerContext;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import org.junit.jupiter.api.Test;
import java.lang.reflect.*;
import java.util.concurrent.atomic.AtomicInteger;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.junit.jupiter.api.Assertions.*;

/** 直接走 handler 的代理调用路径，抛错后不得再通过反射执行相同业务。 */
class ProxyInvocationOnceTest {
    public interface Business { void charge(); }
    @Test void failedAopProxyInvocationDoesNotFallThroughToSecondInvocation() throws Exception {
        java.lang.instrument.Instrumentation instrumentation=ByteBuddyAgent.install();
        ClassReloadingStrategy strategy=ClassReloadingStrategy.of(instrumentation);
        Class.forName(DebugToolsEnvUtils.class.getName(),true,DebugToolsEnvUtils.class.getClassLoader());
        new ByteBuddy().redefine(DebugToolsEnvUtils.class).method(named("isAopProxy")).intercept(FixedValue.value(true)).make().load(DebugToolsEnvUtils.class.getClassLoader(),strategy);
        try {
            AtomicInteger count=new AtomicInteger();
            Object proxy=Proxy.newProxyInstance(getClass().getClassLoader(),new Class<?>[]{Business.class},(instance,method,arguments)->{count.incrementAndGet();throw new IllegalStateException("business failed");});
            Method run=RunTargetMethodRequestHandler.class.getDeclaredMethod("run",Method.class,Object.class,Object[].class,RunDTO.class,ChannelHandlerContext.class,Boolean.class);run.setAccessible(true);
            InvocationTargetException error=assertThrows(InvocationTargetException.class,()->run.invoke(RunTargetMethodRequestHandler.INSTANCE,Business.class.getMethod("charge"),proxy,new Object[0],new RunDTO(),null,false));
            assertEquals("business failed",error.getCause().getMessage());assertEquals(1,count.get());
        } finally { strategy.reset(DebugToolsEnvUtils.class); }
    }
}
