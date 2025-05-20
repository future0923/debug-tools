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
package io.github.future0923.debug.tools.base.classloader;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author future0923
 */
public class DebugToolsExtensionClassLoader extends URLClassLoader {

    private static final Logger logger = Logger.getLogger(DebugToolsExtensionClassLoader.class);

    public DebugToolsExtensionClassLoader(URL[] urls, ClassLoader appClassLoader) {
        super(urls, appClassLoader);
        logger.debug("DebugToolsExtensionClassLoader parent is " + appClassLoader);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception ignore) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }
}