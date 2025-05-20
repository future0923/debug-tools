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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 清除Spring中静态缓存
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ResetSpringStaticCaches {
    private static final Logger LOGGER = Logger.getLogger(ResetSpringStaticCaches.class);

    /**
     * 清除 DefaultListableBeanFactory 中的缓存
     */
    public static void resetBeanNamesByType(DefaultListableBeanFactory defaultListableBeanFactory) {
        // 清除按类型查找的单例 Bean 的名称的缓存
        try {
            Field field = DefaultListableBeanFactory.class.getDeclaredField("singletonBeanNamesByType");
            field.setAccessible(true);
            Map singletonBeanNamesByType = (Map) field.get(defaultListableBeanFactory);
            singletonBeanNamesByType.clear();
        } catch (Exception e) {
            LOGGER.trace("Unable to clear DefaultListableBeanFactory.singletonBeanNamesByType cache (is Ok for pre 3.1.2 Spring version)", e);
        }
        // 清除按类型查找的所有类型(单例、原型、等等)的 Bean 名称的缓存
        try {
            Field field = DefaultListableBeanFactory.class.getDeclaredField("allBeanNamesByType");
            field.setAccessible(true);
            Map allBeanNamesByType = (Map) field.get(defaultListableBeanFactory);
            allBeanNamesByType.clear();
        } catch (Exception e) {
            LOGGER.trace("Unable to clear allBeanNamesByType cache (is Ok for pre 3.2 Spring version)");
        }
        // 清除按类型查找的非单例 Bean 名称的缓存
        try {
            Field field = DefaultListableBeanFactory.class.getDeclaredField("nonSingletonBeanNamesByType");
            field.setAccessible(true);
            Map nonSingletonBeanNamesByType = (Map) field.get(defaultListableBeanFactory);
            nonSingletonBeanNamesByType.clear();
        } catch (Exception e) {
            LOGGER.debug("Unable to clear nonSingletonBeanNamesByType cache (is Ok for pre 3.2 Spring version)");
        }

    }

    /**
     * 清除所有缓存
     */
    public static void reset() {
        resetTypeVariableCache();
        resetAnnotationUtilsCache();
        resetReflectionUtilsCache();
        resetResolvableTypeCache();
        resetPropertyCache();
        CachedIntrospectionResults.clearClassLoader(ResetSpringStaticCaches.class.getClassLoader());
    }

    private static void resetResolvableTypeCache() {
        ReflectionHelper.invokeNoException(null, "org.springframework.core.ResolvableType", ResetSpringStaticCaches.class.getClassLoader(), "clearCache", new Class<?>[] {});
    }

    private static void resetTypeVariableCache() {
        try {
            Field field = GenericTypeResolver.class.getDeclaredField("typeVariableCache");
            field.setAccessible(true);
            Map<Class, Map> typeVariableCache = (Map<Class, Map>) field.get(null);
            typeVariableCache.clear();
            LOGGER.trace("Cache cleared: GenericTypeResolver.typeVariableCache");
        } catch (Exception e) {
            throw new IllegalStateException("Unable to clear GenericTypeResolver.typeVariableCache", e);
        }
    }

    private static void resetReflectionUtilsCache() {
        ReflectionHelper.invokeNoException(null, "org.springframework.util.ReflectionUtils", ResetSpringStaticCaches.class.getClassLoader(), "clearCache", new Class<?>[] {});

        Map declaredMethodsCache = (Map) ReflectionHelper.getNoException(null, ReflectionUtils.class, "declaredMethodsCache");
        if (declaredMethodsCache != null) {
            declaredMethodsCache.clear();
            LOGGER.trace("Cache cleared: ReflectionUtils.declaredMethodsCache");
        } else {
            LOGGER.trace("Cache NOT cleared: ReflectionUtils.declaredMethodsCache not exists");
        }
    }

    private static void resetAnnotationUtilsCache() {
        ReflectionHelper.invokeNoException(null, "org.springframework.core.annotation.AnnotationUtils", ResetSpringStaticCaches.class.getClassLoader(), "clearCache", new Class<?>[] {});

        Map annotatedInterfaceCache = (Map) ReflectionHelper.getNoException(null, AnnotationUtils.class, "annotatedInterfaceCache");
        if (annotatedInterfaceCache != null) {
            annotatedInterfaceCache.clear();
            LOGGER.trace("Cache cleared: AnnotationUtils.annotatedInterfaceCache");
        } else {
            LOGGER.trace("Cache NOT cleared: AnnotationUtils.annotatedInterfaceCache not exists in target Spring verion (pre 3.1.x)");
        }

        Map findAnnotationCache = (Map) ReflectionHelper.getNoException(null, AnnotationUtils.class, "findAnnotationCache");
        if (findAnnotationCache != null) {
            findAnnotationCache.clear();
            LOGGER.trace("Cache cleared: AnnotationUtils.findAnnotationCache");
        } else {
            LOGGER.trace("Cache NOT cleared: AnnotationUtils.findAnnotationCache not exists in target Spring version (pre 4.1)");
        }

    }

    private static void resetPropertyCache() {
        try {
            ClassLoader classLoader = ResetSpringStaticCaches.class.getClassLoader();
            Map annotationCache = (Map) ReflectionHelper.get(null, classLoader.loadClass("org.springframework.core.convert.Property"), "annotationCache");
            annotationCache.clear();
            LOGGER.trace("Cache cleared: Property.annotationCache");
        } catch (Exception e) {
            LOGGER.trace("Unable to clear Property.annotationCache (ok before Spring 3.2.x)", e);
        }
    }
}
