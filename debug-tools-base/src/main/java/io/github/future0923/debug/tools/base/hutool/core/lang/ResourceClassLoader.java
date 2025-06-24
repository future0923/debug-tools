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
package io.github.future0923.debug.tools.base.hutool.core.lang;

import io.github.future0923.debug.tools.base.hutool.core.io.resource.Resource;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 资源类加载器，可以加载任意类型的资源类
 *
 * @param <T> {@link Resource}接口实现类
 * @author looly, lzpeng
 * @since 5.5.2
 */
public class ResourceClassLoader<T extends Resource> extends SecureClassLoader {

	private final Map<String, T> resourceMap;
	/**
	 * 缓存已经加载的类
	 */
	private final Map<String, Class<?>> cacheClassMap;

	/**
	 * 构造
	 *
	 * @param parentClassLoader 父类加载器，null表示默认当前上下文加载器
	 * @param resourceMap       资源map
	 */
	public ResourceClassLoader(ClassLoader parentClassLoader, Map<String, T> resourceMap) {
		super(ObjectUtil.defaultIfNull(parentClassLoader, ClassLoaderUtil::getClassLoader));
		this.resourceMap = ObjectUtil.defaultIfNull(resourceMap, () -> new HashMap<>());
		this.cacheClassMap = new HashMap<>();
	}

	/**
	 * 增加需要加载的类资源
	 *
	 * @param resource 资源，可以是文件、流或者字符串
	 * @return this
	 */
	public ResourceClassLoader<T> addResource(T resource) {
		this.resourceMap.put(resource.getName(), resource);
		return this;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		final Class<?> clazz = cacheClassMap.computeIfAbsent(name, this::defineByName);
		if (clazz == null) {
			return super.findClass(name);
		}
		return clazz;
	}

	/**
	 * 从给定资源中读取class的二进制流，然后生成类<br>
	 * 如果这个类资源不存在，返回{@code null}
	 *
	 * @param name 类名
	 * @return 定义的类
	 */
	private Class<?> defineByName(String name) {
		final Resource resource = resourceMap.get(name);
		if (null != resource) {
			final byte[] bytes = resource.readBytes();
			return defineClass(name, bytes, 0, bytes.length);
		}
		return null;
	}
}
