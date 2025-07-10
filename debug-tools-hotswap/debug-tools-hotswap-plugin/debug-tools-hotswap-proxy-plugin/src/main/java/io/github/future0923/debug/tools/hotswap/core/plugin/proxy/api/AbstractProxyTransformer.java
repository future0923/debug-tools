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

import javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils.ProxyClassSignatureHelper;

/**
 * @author future0923
 */
public abstract class AbstractProxyTransformer {

    public AbstractProxyTransformer(Class<?> classBeingRedefined, ClassPool classPool) {
        super();
        this.classBeingRedefined = classBeingRedefined;
        this.classPool = classPool;
    }

    protected ProxyBytecodeGenerator generator;
    protected ProxyBytecodeTransformer transformer;
    protected Class<?> classBeingRedefined;
    protected ClassPool classPool;

    protected ProxyBytecodeGenerator getGenerator() {
        if (generator == null) {
            generator = createGenerator();
        }
        return generator;
    }

    protected ProxyBytecodeTransformer getTransformer() {
        if (transformer == null) {
            transformer = createTransformer();
        }
        return transformer;
    }

    /**
     * 在此转换器中创建一个新的 ProxyBytecodeGenerator 实例以供使用。
     */
    protected abstract ProxyBytecodeGenerator createGenerator();

    /**
     * 在此转换器中创建一个新的 ProxyBytecodeTransformer 实例以供使用。
     */
    protected abstract ProxyBytecodeTransformer createTransformer();

    /**
     * 检查是否有需要重新定义代理的变化。
     */
    protected boolean isTransformingNeeded() {
        return ProxyClassSignatureHelper.isNonSyntheticPoolClassOrParentDifferent(classBeingRedefined, classPool);
    }

}
