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
 * @author future0923
 */
public class Interceptor {

    @Advice.OnMethodEnter
    static void invokeBeforeEachMethod(@Advice.Origin Class<?> clazz,
                                       @Advice.Origin("#m#s") String method,
                                       @Advice.AllArguments Object[] args) {
        if ("org.apache.ibatis.binding.MapperProxy".equals(clazz.getName())) {
            Method invokeMethod = (Method) args[1];
            if (!Object.class.equals(invokeMethod.getDeclaringClass())) {
                Class<?> declaringClass = invokeMethod.getDeclaringClass();
                MethodTrace.enterMyBatis(declaringClass.getName(), declaringClass.getSimpleName(), invokeMethod.getName());
            }
            return;
        }
        MethodTrace.enterMethod(clazz.getName(), clazz.getSimpleName(), method);
    }

    @Advice.OnMethodExit
    static void invokeWhileExitingEachMethod() {
        MethodTrace.exit();
    }
}
