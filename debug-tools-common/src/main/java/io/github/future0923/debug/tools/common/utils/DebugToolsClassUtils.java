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
package io.github.future0923.debug.tools.common.utils;

import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 类工具类 <br>
 */
public class DebugToolsClassUtils extends ClassUtil {

    private static ClassLoader classLoader;

    public static void setClassLoader(ClassLoader classLoader) {
        DebugToolsClassUtils.classLoader = classLoader;
    }

    public static Class<?> loadDebugToolsClass(String name) throws ClassNotFoundException {
        if (null == classLoader) {
            return ClassUtil.loadClass(name);
        }
        return classLoader.loadClass(name);
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader) {
        return ClassLoaderUtil.loadClass(className, classLoader, true);
    }

    public static String getSimpleName(String className) {
        return DebugToolsStringUtils.getSimpleName(className);
    }

    /**
     * 根据类全路径获取类信息
     *
     * @param className 类全路径集合
     * @return 类信息集合
     */
    public static Class<?>[] getTypes(List<String> className) {
        return className.stream().map(DebugToolsClassUtils::loadClass).toArray(Class[]::new);
    }

    public static List<Field> getAllDeclaredFields(Class<?> clazz) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        Class<?> tmp = clazz;
        List<Field> fields = new LinkedList<>();
        Set<String> fieldName = new HashSet<>();
        do {
            for (Field field : getDeclaredFields(tmp)) {
                if (fieldName.contains(field.getName())) {
                    continue;
                }
                fieldName.add(field.getName());
                fields.add(field);
            }
            tmp = tmp.getSuperclass();
        } while (tmp != null);
        return fields;
    }
}
