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
