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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

/**
 * 可以一步完成的代理转换
 *
 * @author future0923
 */
public abstract class SingleStepProxyTransformer extends AbstractProxyTransformer {

    private static final Logger LOGGER = Logger.getLogger(SingleStepProxyTransformer.class);

    protected byte[] classfileBuffer;

    public SingleStepProxyTransformer(Class<?> classBeingRedefined, ClassPool classPool, byte[] classfileBuffer) {
        super(classBeingRedefined, classPool);
        this.classfileBuffer = classfileBuffer;
    }

    /**
     * 处理当前的转换状态
     */
    public byte[] transformRedefine() throws Exception {
        if (!isTransformingNeeded()) {
            return classfileBuffer;
        }
        classfileBuffer = getTransformer().transform(getGenerator().generate());
        LOGGER.reload("Class '{}' has been reloaded.", classBeingRedefined.getName());
        return classfileBuffer;
    }
}
