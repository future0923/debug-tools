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
