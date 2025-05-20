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
