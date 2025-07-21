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

import io.github.future0923.debug.tools.base.trace.MethodTrace;
import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

/**
 * 追踪方法拦截器
 *
 * @author future0923
 */
public class TraceMethodInterceptor {

    /**
     * 方法执行之前
     *
     * @param clazz  原始类
     * @param method 原始方法
     * @param args   方法入参
     */
    @Advice.OnMethodEnter
    static void invokeBeforeEachMethod(@Advice.Origin Class<?> clazz,
                                       @Advice.Origin Method method,
                                       @Advice.AllArguments Object[] args) {
        if ("org.apache.ibatis.binding.MapperProxy".equals(clazz.getName())) {
            Method invokeMethod = (Method) args[1];
            if (!Object.class.equals(invokeMethod.getDeclaringClass())) {
                Class<?> declaringClass = invokeMethod.getDeclaringClass();
                MethodTrace.enterMyBatis(declaringClass.getName(), declaringClass.getSimpleName(), invokeMethod.getName(), MethodTrace.genMethodSignature(invokeMethod));
            }
            return;
        }
        MethodTrace.enterMethod(clazz.getName(), clazz.getSimpleName(), method.getName(), MethodTrace.genMethodSignature(method));
    }

    /**
     * 方法执行之后
     */
    @Advice.OnMethodExit
    static void invokeWhileExitingEachMethod() {
        MethodTrace.exit();
    }
}
