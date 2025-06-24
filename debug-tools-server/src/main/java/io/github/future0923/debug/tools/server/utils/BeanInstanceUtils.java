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
package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.vm.JvmToolsUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author future0923
 */
public class BeanInstanceUtils {

    public static Object getInstance(Class<?> targetClass, Method targetMethod) {
        Object instance = getInstance(targetClass);
        if (!Modifier.isPublic(targetMethod.getModifiers())) {
            return DebugToolsEnvUtils.getTargetObject(instance);
        } else {
            return instance;
        }
    }

    public static <T> T[] getInstances(Class<T> targetClass) {
        return JvmToolsUtils.getInstances(targetClass);
    }

    /**
     * 获取实例对象
     * <p>优先通过spring 上下文获取
     * <p>获取不到从solon 上下文获取
     * <p>获取不到从jvm中获取，如果有多个取第最后一个
     * <p>获取不到调用构造方法创建
     */
    public static Object getInstance(Class<?> clazz) {
        try {
            Object firstBean = DebugToolsEnvUtils.getLastBean(clazz);
            if (firstBean != null) {
                return firstBean;
            }
        } catch (Throwable ignored) {
            // 加载不到从JVM中获取
        }
        Object[] instances = JvmToolsUtils.getInstances(clazz);
        if (instances.length == 0) {
            return instantiate(clazz);
        } else {
            return instances[instances.length - 1];
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T instantiate(Class<T> clazz) {
        if (clazz == null || clazz.isInterface()) {
            throw new IllegalArgumentException("Specified class is null or interface. " + clazz);
        }
        Optional<Constructor<?>> noArgConstructorOpt = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0).findFirst();
        Object obj;
        try {
            if (noArgConstructorOpt.isPresent()) {
                Constructor<?> constructor = noArgConstructorOpt.get();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                obj = constructor.newInstance();
            } else {
                Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                Object[] objects = IntStream.range(0, constructor.getParameterCount()).mapToObj(i -> null).toArray();
                obj = constructor.newInstance(objects);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("instantiate Exception" + clazz, e);
        }
        return (T) obj;
    }
}
