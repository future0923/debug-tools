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
