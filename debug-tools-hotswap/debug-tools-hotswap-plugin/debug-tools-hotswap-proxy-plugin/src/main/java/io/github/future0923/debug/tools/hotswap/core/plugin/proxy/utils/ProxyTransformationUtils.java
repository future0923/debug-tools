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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author future0923
 */
public class ProxyTransformationUtils {

    private static final Logger LOGGER = Logger.getLogger(ProxyTransformationUtils.class);
    private static final Map<ClassLoader, ClassPool> classPoolMap = new WeakHashMap<>(3);

    /**
     * 为每个类加载器创建一个 ClassPool 并将其缓存。
     */
    public static ClassPool getClassPool(ClassLoader classLoader) {
        ClassPool classPool = classPoolMap.get(classLoader);
        if (classPool == null) {
            synchronized (classPoolMap) {
                classPool = classPoolMap.get(classLoader);
                if (classPool == null) {
                    classPool = createClassPool(classLoader);
                    classPoolMap.put(classLoader, classPool);
                }
            }
        }
        return classPool;
    }

    /**
     * 使用提供的类加载器创建一个 ClassPool。
     */
    public static ClassPool createClassPool(final ClassLoader classLoader) {
        ClassPool cp = new ClassPool() {
            @Override
            public ClassLoader getClassLoader() {
                return classLoader;
            }
        };
        cp.appendSystemPath();
        if (classLoader != null) {
            LOGGER.trace("Adding loader classpath " + classLoader);
            cp.appendClassPath(new LoaderClassPath(classLoader));
        }
        return cp;
    }

    private static final int BUFFER_SIZE = 8192;

    public static byte[] copyToByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
            return out.toByteArray();
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }
}
