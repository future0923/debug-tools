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
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeGenerator;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.SingleStepProxyTransformer;

/**
 * 重新定义了 Cglib 增强器代理类。使用 CglibEnhancerProxyBytecodeGenerator 进行字节码生成。
 *
 * @author future0923
 */
public class CglibEnhancerProxyTransformer extends SingleStepProxyTransformer {

    private final GeneratorParams params;
    private final ClassLoader loader;

    public CglibEnhancerProxyTransformer(Class<?> classBeingRedefined,
                                         ClassPool classPool,
                                         byte[] classfileBuffer,
                                         ClassLoader loader,
                                         GeneratorParams params) {
        super(classBeingRedefined, classPool, classfileBuffer);
        this.loader = loader;
        this.params = params;
    }

    public static byte[] transform(Class<?> classBeingRedefined,
                                   ClassPool classPool,
                                   byte[] classfileBuffer,
                                   ClassLoader loader,
                                   GeneratorParams params) throws Exception {
        return new CglibEnhancerProxyTransformer(classBeingRedefined, classPool, classfileBuffer, loader, params).transformRedefine();
    }

    @Override
    protected ProxyBytecodeGenerator createGenerator() {
        return new CglibEnhancerProxyBytecodeGenerator(params, loader);
    }

    @Override
    protected ProxyBytecodeTransformer createTransformer() {
        return new CglibProxyBytecodeTransformer(classPool);
    }
}
