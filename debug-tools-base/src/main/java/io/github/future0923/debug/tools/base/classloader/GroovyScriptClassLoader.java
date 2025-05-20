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

/**
 * Groovy运行的父类，用来兼容应用程序的类和DebugTools程序的类的加载
 *
 * @author future0923
 */
public class GroovyScriptClassLoader extends ClassLoader {

    private ClassLoaderWrapper defaultClassLoader;

    private static GroovyScriptClassLoader classLoader;

    public static GroovyScriptClassLoader getInstance() {
        return classLoader;
    }

    public static GroovyScriptClassLoader init(ClassLoader classLoader) {
        GroovyScriptClassLoader.classLoader = new GroovyScriptClassLoader(classLoader);
        return getInstance();
    }

    private GroovyScriptClassLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        this.defaultClassLoader = new ClassLoaderWrapper(defaultClassLoader);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            return defaultClassLoader.loadClass(name, resolve);
        }
    }
}
