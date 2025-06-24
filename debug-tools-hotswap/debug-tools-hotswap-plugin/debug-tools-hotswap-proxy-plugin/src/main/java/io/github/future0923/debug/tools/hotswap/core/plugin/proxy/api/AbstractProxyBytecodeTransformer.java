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

import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author future0923
 */
public abstract class AbstractProxyBytecodeTransformer implements ProxyBytecodeTransformer{

    private final ClassPool classPool;

    public AbstractProxyBytecodeTransformer(ClassPool classPool) {
        this.classPool = classPool;
    }

    public byte[] transform(byte[] byteCode) throws Exception {
        CtClass cc = classPool.makeClass(new ByteArrayInputStream(byteCode), false);
        try {
            String initFieldName = INIT_FIELD_PREFIX + generateRandomString();
            addStaticInitStateField(cc, initFieldName);

            String initCode = getInitCall(cc, initFieldName);

            addInitCallToMethods(cc, initFieldName, initCode);
            return cc.toBytecode();
        } finally {
            cc.detach();
        }
    }

    /**
     * 构建应执行的Java代码字符串，以初始化代理。
     */
    protected abstract String getInitCall(CtClass cc, String random) throws Exception;

    protected String generateRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 将initCall作为Java代码添加到类的所有非静态方法中 clinitFieldName 为false。设置 clinitFieldName 的责任在initCall上。
     */
    protected void addInitCallToMethods(CtClass cc, String clinitFieldName, String initCall) throws Exception {
        CtMethod[] methods = cc.getDeclaredMethods();
        for (CtMethod ctMethod : methods) {
            if (!ctMethod.isEmpty() && !Modifier.isStatic(ctMethod.getModifiers())) {
                ctMethod.insertBefore("if(!" + clinitFieldName + "){synchronized(" + cc.getName() + ".class){if(!"
                        + clinitFieldName + "){" + initCall + "}}}");
            }
        }
    }

    /**
     * 在类中添加一个静态布尔字段，用于指示初始化状态。
     */
    protected void addStaticInitStateField(CtClass cc, String clinitFieldName) throws Exception {
        CtField f = new CtField(CtClass.booleanType, clinitFieldName, cc);
        f.setModifiers(Modifier.PRIVATE | Modifier.STATIC);
        // init value "true" will be inside clinit, so the field wont actually be initialized on redefinition
        cc.addField(f, "true");
    }
}
