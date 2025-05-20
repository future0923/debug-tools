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
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.AbstractProxyBytecodeTransformer;

/**
 * 转换新的 Cglib 代理定义的字节码，使其在首次访问其中一个方法时初始化。
 *
 * @author future0923
 */
public class CglibProxyBytecodeTransformer extends AbstractProxyBytecodeTransformer {

    public CglibProxyBytecodeTransformer(ClassPool classPool) {
        super(classPool);
    }

    @Override
    protected String getInitCall(CtClass cc, String initFieldName) throws Exception {
        CtMethod[] methods = cc.getDeclaredMethods();
        StringBuilder strB = new StringBuilder();
        for (CtMethod ctMethod : methods) {
            if (ctMethod.getName().startsWith("CGLIB$STATICHOOK")) {
                ctMethod.insertAfter(initFieldName + "=true;");
                strB.insert(0, ctMethod.getName() + "();");
                break;
            }
        }

        if (strB.length() == 0) {
            throw new RuntimeException("Could not find CGLIB$STATICHOOK method");
        }
        return strB.toString() + "CGLIB$BIND_CALLBACKS(this);";
    }
}
