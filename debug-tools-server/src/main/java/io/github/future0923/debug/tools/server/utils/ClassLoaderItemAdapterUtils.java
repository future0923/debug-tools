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

import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.server.http.itemAdapter.ClassLoaderItemAdapterEnum;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 *
 *
 * @author iwillmissu
 * @date 2025/9/24 16:16:34
 **/
public class ClassLoaderItemAdapterUtils {

    /**
     * classloader -> item
     */
    public static final ConcurrentHashMap<ClassLoader, AllClassLoaderRes.Item> CLASS_LOADER_ITEM_MAP = new ConcurrentHashMap<>();

    /**
     * classloader -> adapter
     */
    public static final ConcurrentHashMap<String, Function<ClassLoader, AllClassLoaderRes.Item>> CLASS_LOADER_ITEM_ADAPTER_MAP = new ConcurrentHashMap<>();

    /**
     * 获取缓存适配器
     *
     * @param classLoaderName 类加载器名称
     * @return {@link Function }<{@link ClassLoader }, {@link AllClassLoaderRes.Item }>
     */
    public static Function<ClassLoader, AllClassLoaderRes.Item> getCacheAdapter(String classLoaderName) {
        return CLASS_LOADER_ITEM_ADAPTER_MAP.computeIfAbsent(classLoaderName, ClassLoaderItemAdapterUtils::getTrueAdapter);
    }

    /**
     * 获取真正适配器
     *
     * @param classLoaderName 类加载器名称
     * @return {@link Function }<{@link ClassLoader }, {@link AllClassLoaderRes.Item }>
     */
    public static Function<ClassLoader, AllClassLoaderRes.Item> getTrueAdapter(String classLoaderName) {
        return Arrays.stream(ClassLoaderItemAdapterEnum.values())
                .filter(anObject -> classLoaderName.equals(anObject.getClassLoaderName()))
                .findFirst()
                .orElse(ClassLoaderItemAdapterEnum.DEFAULT)
                .getAdapter();
    }

    /**
     * 执行适配
     *
     * @param classLoader 类加载器
     * @return {@link AllClassLoaderRes.Item }
     */
    public static AllClassLoaderRes.Item Adapted(ClassLoader classLoader) {
        return CLASS_LOADER_ITEM_MAP.computeIfAbsent(classLoader,
                k -> getCacheAdapter(k.getClass().getName()).apply(classLoader));

    }

    private ClassLoaderItemAdapterUtils() {
    }

}
