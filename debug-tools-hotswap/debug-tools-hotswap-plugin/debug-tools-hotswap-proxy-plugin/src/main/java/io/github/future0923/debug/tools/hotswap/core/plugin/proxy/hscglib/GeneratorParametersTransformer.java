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
