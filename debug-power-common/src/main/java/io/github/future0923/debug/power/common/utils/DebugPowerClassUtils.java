package io.github.future0923.debug.power.common.utils;

import cn.hutool.core.util.ClassUtil;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 类工具类 <br>
 */
public class DebugPowerClassUtils extends ClassUtil {

    private static ClassLoader classLoader;

    public static void setClassLoader(ClassLoader classLoader) {
        DebugPowerClassUtils.classLoader = classLoader;
    }

    public static Class<?> loadDebugPowerClass(String name) throws ClassNotFoundException {
        if (null == classLoader) {
            return ClassUtil.loadClass(name);
        }
        return classLoader.loadClass(name);
    }

    public static String getSimpleName(String className) {
        return DebugPowerStringUtils.getSimpleName(className);
    }

    /**
     * 根据类全路径获取类信息
     *
     * @param className 类全路径集合
     * @return 类信息集合
     */
    public static Class<?>[] getTypes(List<String> className) {
        return className.stream().map(DebugPowerClassUtils::loadClass).toArray(Class[]::new);
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
