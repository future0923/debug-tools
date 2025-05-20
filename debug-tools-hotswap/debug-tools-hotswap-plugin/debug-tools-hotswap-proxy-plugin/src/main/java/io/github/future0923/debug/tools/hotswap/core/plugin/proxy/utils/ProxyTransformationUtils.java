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
