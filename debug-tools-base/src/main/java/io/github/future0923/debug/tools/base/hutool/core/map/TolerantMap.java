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
package io.github.future0923.debug.tools.base.hutool.core.map;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 一个可以提供默认值的Map
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author pantao, looly
 */
public class TolerantMap<K, V> extends MapWrapper<K, V> {
	private static final long serialVersionUID = -4158133823263496197L;

	private final V defaultValue;

	/**
	 * 构造
	 *
	 * @param defaultValue 默认值
	 */
	public TolerantMap(V defaultValue) {
		this(new HashMap<>(), defaultValue);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始容量
	 * @param loadFactor      增长因子
	 * @param defaultValue    默认值
	 */
	public TolerantMap(int initialCapacity, float loadFactor, V defaultValue) {
		this(new HashMap<>(initialCapacity, loadFactor), defaultValue);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始容量
	 * @param defaultValue    默认值
	 */
	public TolerantMap(int initialCapacity, V defaultValue) {
		this(new HashMap<>(initialCapacity), defaultValue);
	}

	/**
	 * 构造
	 *
	 * @param map          Map实现
	 * @param defaultValue 默认值
	 */
	public TolerantMap(Map<K, V> map, V defaultValue) {
		super(map);
		this.defaultValue = defaultValue;
	}

	/**
	 * 构建TolerantMap
	 *
	 * @param map          map实现
	 * @param defaultValue 默认值
	 * @param <K>          键类型
	 * @param <V>          值类型
	 * @return TolerantMap
	 */
	public static <K, V> TolerantMap<K, V> of(Map<K, V> map, V defaultValue) {
		return new TolerantMap<>(map, defaultValue);
	}

	@Override
	public V get(Object key) {
		return getOrDefault(key, defaultValue);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (false == super.equals(o)) {
			return false;
		}
		final TolerantMap<?, ?> that = (TolerantMap<?, ?>) o;
		return getRaw().equals(that.getRaw())
				&& Objects.equals(defaultValue, that.defaultValue);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getRaw(), defaultValue);
	}

	@Override
	public String toString() {
		return "TolerantMap{" + "map=" + getRaw() + ", defaultValue=" + defaultValue + '}';
	}
}
