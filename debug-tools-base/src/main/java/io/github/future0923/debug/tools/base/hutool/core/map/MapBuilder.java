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
package io.github.future0923.debug.tools.base.hutool.core.map;


import io.github.future0923.debug.tools.base.hutool.core.builder.Builder;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Map创建类
 *
 * @param <K> Key类型
 * @param <V> Value类型
 * @since 3.1.1
 */
public class MapBuilder<K, V> implements Builder<Map<K, V>> {
	private static final long serialVersionUID = 1L;

	private final Map<K, V> map;

	/**
	 * 创建Builder，默认HashMap实现
	 *
	 * @param <K> Key类型
	 * @param <V> Value类型
	 * @return MapBuilder
	 * @since 5.3.0
	 */
	public static <K, V> MapBuilder<K, V> create() {
		return create(false);
	}

	/**
	 * 创建Builder
	 *
	 * @param <K>      Key类型
	 * @param <V>      Value类型
	 * @param isLinked true创建LinkedHashMap，false创建HashMap
	 * @return MapBuilder
	 * @since 5.3.0
	 */
	public static <K, V> MapBuilder<K, V> create(boolean isLinked) {
		return create(MapUtil.newHashMap(isLinked));
	}

	/**
	 * 创建Builder
	 *
	 * @param <K> Key类型
	 * @param <V> Value类型
	 * @param map Map实体类
	 * @return MapBuilder
	 * @since 3.2.3
	 */
	public static <K, V> MapBuilder<K, V> create(Map<K, V> map) {
		return new MapBuilder<>(map);
	}

	/**
	 * 链式Map创建类
	 *
	 * @param map 要使用的Map实现类
	 */
	public MapBuilder(Map<K, V> map) {
		this.map = map;
	}

	/**
	 * 链式Map创建
	 *
	 * @param k Key类型
	 * @param v Value类型
	 * @return 当前类
	 */
	public MapBuilder<K, V> put(K k, V v) {
		map.put(k, v);
		return this;
	}

	/**
	 * 链式Map创建
	 *
	 * @param condition put条件
	 * @param k         Key类型
	 * @param v         Value类型
	 * @return 当前类
	 * @since 5.7.5
	 */
	public MapBuilder<K, V> put(boolean condition, K k, V v) {
		if (condition) {
			put(k, v);
		}
		return this;
	}

	/**
	 * 链式Map创建
	 *
	 * @param condition put条件
	 * @param k         Key类型
	 * @param supplier  Value类型结果提供方
	 * @return 当前类
	 * @since 5.7.5
	 */
	public MapBuilder<K, V> put(boolean condition, K k, Supplier<V> supplier) {
		if (condition) {
			put(k, supplier.get());
		}
		return this;
	}

	/**
	 * 链式Map创建
	 *
	 * @param map 合并map
	 * @return 当前类
	 */
	public MapBuilder<K, V> putAll(Map<K, V> map) {
		this.map.putAll(map);
		return this;
	}

	/**
	 * 清空Map
	 *
	 * @return this
	 * @since 5.7.23
	 */
	public MapBuilder<K, V> clear() {
		this.map.clear();
		return this;
	}

	/**
	 * 创建后的map
	 *
	 * @return 创建后的map
	 */
	public Map<K, V> map() {
		return map;
	}

	/**
	 * 创建后的map
	 *
	 * @return 创建后的map
	 * @since 3.3.0
	 */
	@Override
	public Map<K, V> build() {
		return map();
	}

	/**
	 * 将map转成字符串
	 *
	 * @param separator         entry之间的连接符
	 * @param keyValueSeparator kv之间的连接符
	 * @return 连接字符串
	 */
	public String join(String separator, final String keyValueSeparator) {
		return MapUtil.join(this.map, separator, keyValueSeparator);
	}

	/**
	 * 将map转成字符串
	 *
	 * @param separator         entry之间的连接符
	 * @param keyValueSeparator kv之间的连接符
	 * @return 连接后的字符串
	 */
	public String joinIgnoreNull(String separator, final String keyValueSeparator) {
		return MapUtil.joinIgnoreNull(this.map, separator, keyValueSeparator);
	}

	/**
	 * 将map转成字符串
	 *
	 * @param separator         entry之间的连接符
	 * @param keyValueSeparator kv之间的连接符
	 * @param isIgnoreNull      是否忽略null的键和值
	 * @return 连接后的字符串
	 */
	public String join(String separator, final String keyValueSeparator, boolean isIgnoreNull) {
		return MapUtil.join(this.map, separator, keyValueSeparator, isIgnoreNull);
	}

}
