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

import io.github.future0923.debug.tools.base.trace.MethodWatchBuffer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import java.lang.annotation.*;

/** Advice 内联到业务字节码，只引用应用可见的 base 缓冲器。 */
public final class MethodWatchAdvice {
    @Retention(RetentionPolicy.RUNTIME) @Target(ElementType.PARAMETER)
    public @interface WatchId { }
    @Advice.OnMethodEnter(suppress=Throwable.class)
    public static long enter(@WatchId String id,@Advice.AllArguments Object[] arguments) { return MethodWatchBuffer.enter(id,arguments); }
    @Advice.OnMethodExit(onThrowable=Throwable.class,suppress=Throwable.class)
    public static void exit(@WatchId String id,@Advice.Enter long sequence,@Advice.Return(typing=Assigner.Typing.DYNAMIC) Object result,@Advice.Thrown Throwable error) { MethodWatchBuffer.exit(id,sequence,result,error); }
}
