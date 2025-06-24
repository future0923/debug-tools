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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;
import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.MethodInfo;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author future0923
 */
public class GeneratorParametersTransformer {

    private static final Logger LOGGER = Logger.getLogger(GeneratorParametersTransformer.class);

    private static final Map<ClassLoader, WeakReference<Map<String, Object>>> classLoaderMaps = new WeakHashMap<ClassLoader, WeakReference<Map<String, Object>>>();

    /**
     * 添加字节码生成调用参数记录功能
     */
    public static CtClass transform(CtClass cc) throws Exception {
        if (isGeneratorStrategy(cc)) {
            for (CtMethod method : cc.getDeclaredMethods()) {
                if (!Modifier.isAbstract(method.getModifiers()) && method.getName().equals("generate")
                        && method.getMethodInfo().getDescriptor().endsWith(";)[B")) {
                    cc.defrost();
                    method.insertAfter("io.github.future0923.debug.tools.hotswap.core.plugin.proxy.hscglib.GeneratorParametersRecorder.register($0, $1, $_);");
                }
            }
        }
        return cc;
    }

    /**
     * 判断一个类是否是 Cglib GeneratorStrategy 的子类。
     */
    private static boolean isGeneratorStrategy(CtClass cc) {
        String[] interfaces = cc.getClassFile2().getInterfaces();
        for (String interfaceName : interfaces) {
            // 我们使用类名称字符串，因为一些库将 cglib 重新打包到不同的命名空间以避免冲突。
            if (interfaceName.endsWith(".GeneratorStrategy")) {
                List<MethodInfo> methodInfos = cc.getClassFile2().getMethods();
                for (MethodInfo method : methodInfos) {
                    if (method.getName().equals("generate") && method.getDescriptor().endsWith("[B")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 从类加载器中检索 GeneratorParams 映射。
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> getGeneratorParamsMap(ClassLoader loader) {
        try {
            WeakReference<Map<String, Object>> mapRef;
            synchronized (classLoaderMaps) {
                mapRef = classLoaderMaps.get(loader);
                if (mapRef == null) {
                    if (ClassLoaderHelper.isClassLoaderStarted(loader)) {
                        Map<String, Object> map = (Map<String, Object>) loader
                                .loadClass(GeneratorParametersRecorder.class.getName()).getField("generatorParams")
                                .get(null);
                        mapRef = new WeakReference<Map<String, Object>>(map);
                        classLoaderMaps.put(loader, mapRef);
                    }
                }
            }
            Map<String, Object> map = mapRef != null ? mapRef.get() : null;
            if (map == null) {
                return new HashMap<>();
            }
            return map;
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
                 | ClassNotFoundException e) {
            LOGGER.error("Unable to access field with proxy generation parameters. Proxy redefinition failed.");
            throw new RuntimeException(e);
        }
    }

    /**
     * 从类加载器中检索 GeneratorParams。
     */
    public static GeneratorParams getGeneratorParams(ClassLoader loader, String name) {
        Object generatorParams = getGeneratorParamsMap(loader).get(name);
        if (generatorParams != null) {
            try {
                return GeneratorParams.valueOf(generatorParams);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
