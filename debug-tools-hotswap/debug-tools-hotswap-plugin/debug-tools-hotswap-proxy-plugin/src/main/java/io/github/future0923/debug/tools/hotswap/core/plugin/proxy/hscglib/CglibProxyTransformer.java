/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
