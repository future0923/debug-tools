/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
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
