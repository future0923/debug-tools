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
package io.github.future0923.debug.tools.hotswap.core.util;

import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class JavassistUtil {

    private static final Map<ClassLoader, ClassPool> CLASS_POOL_MAP = new ConcurrentHashMap<>();

    /**
     * 获取javassist ClassPool
     */
    public static ClassPool getClassPool(ClassLoader classLoader) {
        if (classLoader == null) {
            return ClassPool.getDefault();
        }
        return CLASS_POOL_MAP.computeIfAbsent(classLoader, cl -> {
            ClassPool cp = new ClassPool();
            cp.appendSystemPath();
            cp.appendClassPath(new LoaderClassPath(cl));
            return cp;
        });
    }

    /**
     * 创建javassist CtClass
     */
    public static CtClass createCtClass(ClassLoader classLoader, byte[] bytes) throws IOException {
        return getClassPool(classLoader).makeClass(new ByteArrayInputStream(bytes));
    }

    /**
     * 插入修改CtClass的，再从ClassPool获取时可以拿到最新的
     */
    public static void insertClassPath(ClassLoader classLoader, String name, CtClass ctClass) throws IOException, CannotCompileException {
        getClassPool(classLoader).insertClassPath(new ByteArrayClassPath(name, ctClass.toBytecode()));
    }

}
