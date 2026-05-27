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

import io.github.future0923.debug.tools.base.around.RunMethodAround;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.compiler.DynamicCompiler;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Map;

final class RunMethodAroundInvoker {

    private String methodAroundContentIdentity;

    Invocation prepare(RunDTO runDTO, ClassLoader classLoader, Object[] targetMethodArgs) throws Exception {
        if (StrUtil.isBlank(runDTO.getMethodAroundContent())) {
            return Invocation.EMPTY;
        }
        Class<?> aroundClass = DebugToolsClassUtils.loadClass(RunMethodAround.class.getName(), classLoader);
        if (!StrUtil.equals(methodAroundContentIdentity, runDTO.getMethodAroundContentIdentity())) {
            methodAroundContentIdentity = runDTO.getMethodAroundContentIdentity();
            Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
            DynamicCompiler dynamicCompiler = new DynamicCompiler(classLoader);
            dynamicCompiler.addSource(RunMethodAround.class.getName(), runDTO.getMethodAroundContent());
            instrumentation.redefineClasses(new ClassDefinition(aroundClass, dynamicCompiler.buildByteCodes().get(RunMethodAround.class.getName())));
        }
        Object aroundInstance = aroundClass.getConstructor().newInstance();
        Invocation invocation = new ActiveInvocation(runDTO, targetMethodArgs, aroundClass, aroundInstance);
        invocation.onBefore();
        return invocation;
    }

    interface Invocation {

        Invocation EMPTY = new Invocation() {
            @Override
            public void onBefore() {
            }

            @Override
            public void onAfter(Object result) {
            }

            @Override
            public void onException(Throwable throwable) {
            }

            @Override
            public void onFinally(Object result, Throwable throwable) {
            }
        };

        void onBefore();

        void onAfter(Object result);

        void onException(Throwable throwable);

        void onFinally(Object result, Throwable throwable);
    }

    private static final class ActiveInvocation implements Invocation {

        private final RunDTO runDTO;

        private final Object[] targetMethodArgs;

        private final Class<?> aroundClass;

        private final Object aroundInstance;

        private ActiveInvocation(RunDTO runDTO, Object[] targetMethodArgs, Class<?> aroundClass, Object aroundInstance) {
            this.runDTO = runDTO;
            this.targetMethodArgs = targetMethodArgs;
            this.aroundClass = aroundClass;
            this.aroundInstance = aroundInstance;
        }

        @Override
        public void onBefore() {
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onBefore", Map.class, String.class, String.class, String.class, List.class, Object[].class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs
            );
        }

        @Override
        public void onAfter(Object result) {
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onAfter", Map.class, String.class, String.class, String.class, List.class, Object[].class, Object.class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs,
                    result
            );
        }

        @Override
        public void onException(Throwable throwable) {
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onException", Map.class, String.class, String.class, String.class, List.class, Object[].class, Exception.class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs,
                    throwable
            );
        }

        @Override
        public void onFinally(Object result, Throwable throwable) {
            ReflectUtil.invoke(
                    aroundInstance,
                    ReflectUtil.getMethod(aroundClass, "onFinally", Map.class, String.class, String.class, String.class, List.class, Object[].class, Object.class, Exception.class),
                    runDTO.getHeaders(),
                    runDTO.getXxlJobParam(),
                    runDTO.getTargetClassName(),
                    runDTO.getTargetMethodName(),
                    runDTO.getTargetMethodParameterTypes(),
                    targetMethodArgs,
                    result,
                    throwable
            );
        }
    }
}
