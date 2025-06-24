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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.MultistepProxyTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeGenerator;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.TransformationState;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author future0923
 */
public class CglibProxyTransformer extends MultistepProxyTransformer {

    /**
     * 所有类加载器的类转换状态。用于代理类加载器。
     */
    private static final Map<Class<?>, TransformationState> TRANSFORMATION_STATES = Collections.synchronizedMap(new WeakHashMap<>());
    private final GeneratorParams params;

    public CglibProxyTransformer(Class<?> classBeingRedefined,
                                 ClassPool classPool, byte[] classfileBuffer,
                                 GeneratorParams params) {
        super(classBeingRedefined, classPool, classfileBuffer,
                TRANSFORMATION_STATES);
        this.params = params;
    }

    public static byte[] transform(Class<?> classBeingRedefined,
                                   ClassPool classPool,
                                   byte[] classfileBuffer,
                                   GeneratorParams params) throws Exception {
        return new CglibProxyTransformer(classBeingRedefined, classPool, classfileBuffer, params).transformRedefine();
    }

    public static boolean isReloadingInProgress() {
        return !TRANSFORMATION_STATES.isEmpty();
    }

    @Override
    protected ProxyBytecodeGenerator createGenerator() {
        return new CglibProxyBytecodeGenerator(params);
    }

    @Override
    protected ProxyBytecodeTransformer createTransformer() {
        return new CglibProxyBytecodeTransformer(classPool);
    }
}
