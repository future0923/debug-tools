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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * @author future0923
 */
public class SpringBootClassLoaderPatcher {

    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.DefaultListableBeanFactory")
    public static void patchDefaultListableBeanFactory(CtClass clazz) throws NotFoundException, CannotCompileException {
        CtMethod method = clazz.getDeclaredMethod("preInstantiateSingletons");
        String body = "{" +
                "    try {" +
                "            java.lang.ClassLoader classLoader = org.springframework.beans.factory.support.DefaultListableBeanFactory.class.getClassLoader();" +
                "            java.lang.Class pluginManager = classLoader.loadClass(\"" + PluginManager.class.getName() + "\");" +
                "            java.lang.reflect.Method enhanceClassLoader = pluginManager.getDeclaredMethod(\"enhanceClassLoader\", new java.lang.Class[] { java.lang.ClassLoader.class });" +
                "            enhanceClassLoader.setAccessible(true);" +
                "            enhanceClassLoader.invoke(null, new java.lang.Object[] { classLoader });" +
                "    } catch (java.lang.Exception e) {" +
                "            throw new java.lang.RuntimeException(e);" +
                "    }" +
                "}";
        method.insertAfter(body);
    }
}
