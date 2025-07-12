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

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
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
