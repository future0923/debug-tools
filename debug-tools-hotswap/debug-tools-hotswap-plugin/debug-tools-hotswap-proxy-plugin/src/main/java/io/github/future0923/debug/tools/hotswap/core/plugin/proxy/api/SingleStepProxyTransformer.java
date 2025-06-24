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
