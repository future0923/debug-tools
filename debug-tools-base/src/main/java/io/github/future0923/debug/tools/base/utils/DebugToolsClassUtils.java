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
package io.github.future0923.debug.tools.base.utils;

import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    /**
     * 生成JVMS规范的方法描述符
     */
    public static String getMethodDescriptor(Class<?>[] parameterTypes, Class<?> returnType) {
        return getParameterDescriptors(parameterTypes) + ((returnType == void.class) ? "V" : getFieldType(returnType));
    }

    /**
     * 生成JVMS规范的方法参数描述符
     */
    public static String getParameterDescriptors(Class<?>[] parameterTypes) {
        StringBuilder desc = new StringBuilder("(");
        for (Class<?> parameterType : parameterTypes) {
            desc.append(getFieldType(parameterType));
        }
        desc.append(')');
        return desc.toString();
    }

    /**
     * 生成JVMS对应的字段描述符
     */
    private static String getFieldType(Class<?> type) {
        if (type.isPrimitive()) {
            return PrimitiveTypeInfo.get(type).baseTypeString;
        } else if (type.isArray()) {
            return type.getName().replace('.', '/');
        } else {
            return "L" + dotToSlash(type.getName()) + ";";
        }
    }

    /**
     * 私有变量的描述符
     */
    private static class PrimitiveTypeInfo {

        public String baseTypeString;

        public String wrapperClassName;

        public String wrapperValueOfDesc;

        public String unwrapMethodName;

        public String unwrapMethodDesc;

        private static final Map<Class<?>, PrimitiveTypeInfo> table = new HashMap<>();
        static {
            add(byte.class, Byte.class);
            add(char.class, Character.class);
            add(double.class, Double.class);
            add(float.class, Float.class);
            add(int.class, Integer.class);
            add(long.class, Long.class);
            add(short.class, Short.class);
            add(boolean.class, Boolean.class);
        }

        private static void add(Class<?> primitiveClass, Class<?> wrapperClass) {
            table.put(primitiveClass, new PrimitiveTypeInfo(primitiveClass, wrapperClass));
        }

        private PrimitiveTypeInfo(Class<?> primitiveClass, Class<?> wrapperClass) {
            assert primitiveClass.isPrimitive();

            baseTypeString = Array.newInstance(primitiveClass, 0).getClass().getName().substring(1);
            wrapperClassName = dotToSlash(wrapperClass.getName());
            wrapperValueOfDesc = "(" + baseTypeString + ")L" + wrapperClassName + ";";
            unwrapMethodName = primitiveClass.getName() + "Value";
            unwrapMethodDesc = "()" + baseTypeString;
        }

        public static PrimitiveTypeInfo get(Class<?> cl) {
            return table.get(cl);
        }
    }

    public static String dotToSlash(String name) {
        return name.replace('.', '/');
    }

    /**
     * 获取方法唯一标识符key
     *
     * @param className         类名
     * @param methodName        方法名
     * @param methodDescription 方法描述
     * @return 唯一标识符key
     */
    public static String getQualifierMethod(String className, String methodName, String methodDescription) {
        return className + "#" + methodName + methodDescription;
    }
}
