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

import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.utils.ProxyTransformationUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author future0923
 */
public class ParentLastClassLoader extends ClassLoader {

    public static final String[] EXCLUDED_PACKAGES = new String[] { "java.", "javax.", "sun.", "oracle." };

    public ParentLastClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        for (String excludedPackage : EXCLUDED_PACKAGES) {
            if (name.startsWith(excludedPackage))
                return super.loadClass(name, resolve);
        }
        Class<?> clazz = loadClassFromThisClassLoader(name);
        if (clazz == null)
            return super.loadClass(name, resolve);

        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }

    protected Class<?> loadClassFromThisClassLoader(String name) throws ClassNotFoundException {
        Class<?> result = findLoadedClass(name);
        if (result != null) {
            return result;
        }
        byte[] bytes = readClass(name);
        if (bytes != null) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        return null;
    }

    protected byte[] readClass(String name) throws ClassNotFoundException {
        InputStream is = getParent().getResourceAsStream(name.replace('.', '/') + ".class");
        if (is == null) {
            return null;
        }
        try {
            return ProxyTransformationUtils.copyToByteArray(is);
        } catch (IOException ex) {
            throw new ClassNotFoundException("Could not read: " + name, ex);
        }
    }
}
