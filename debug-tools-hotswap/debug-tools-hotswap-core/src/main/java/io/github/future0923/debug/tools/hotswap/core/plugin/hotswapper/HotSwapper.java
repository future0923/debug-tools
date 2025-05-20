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
package io.github.future0923.debug.tools.hotswap.core.plugin.hotswapper;

import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotSwapper {

    public static void swapClasses(Class<?> original, String swap) throws Exception {
        ClassPool classPool = new ClassPool();
        classPool.appendClassPath(new LoaderClassPath(original.getClassLoader()));
        CtClass ctClass = classPool.getAndRename(swap, original.getName());
        reload(original, ctClass.toBytecode());
    }

    private static void reload(Class<?> original, byte[] bytes) {
        Map<Class<?>, byte[]> reloadMap = new HashMap<>();
        reloadMap.put(original, bytes);
        PluginManager.getInstance().hotswap(reloadMap);
    }

    public static Class<?> newClass(String className, String directory, ClassLoader cl){
        try {
            ClassPool classPool = new ClassPool();
            classPool.appendClassPath(new LoaderClassPath(cl));
            CtClass makeClass = classPool.makeClass(className);
            makeClass.writeFile(directory);
            return makeClass.toClass();
        } catch (Throwable ex) {
            Logger.getLogger(HotSwapper.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

}
