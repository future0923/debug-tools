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