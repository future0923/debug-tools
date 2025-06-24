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

import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.ProxyBytecodeGenerator;

import java.lang.reflect.Method;

/**
 * 为Cglib代理创建新字节码。必须加载更改的类
 * 已经在App类加载器中。
 *
 * @author future0923
 */
public class CglibProxyBytecodeGenerator implements ProxyBytecodeGenerator {

    private final GeneratorParams params;

    public CglibProxyBytecodeGenerator(GeneratorParams params) {
        super();
        this.params = params;
    }

    public byte[] generate() throws Exception {
        Method genMethod = getGenerateMethod(params.getGenerator());
        if (genMethod == null) {
            throw new RuntimeException("No generation Method found for redefinition!");
        }
        return (byte[]) genMethod.invoke(params.getGenerator(), params.getParam());
    }

    /**
     * 检索生成并返回字节码的实际方法。
     */
    private Method getGenerateMethod(Object generator) {
        Method[] methods = generator.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("generate")
                    && method.getReturnType().getSimpleName().equals("byte[]")
                    && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        return null;
    }
}
