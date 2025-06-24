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
package io.github.future0923.debug.tools.base.hutool.core.map.multi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 值作为集合List的Map实现，通过调用putValue可以在相同key时加入多个值，多个值用集合表示
 *
 * @author looly
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 4.3.3
 */
public class ListValueMap<K, V> extends AbsCollValueMap<K, V, List<V>> {
	private static final long serialVersionUID = 6044017508487827899L;

	// ------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public ListValueMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 */
	public ListValueMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * 构造
	 *
	 * @param m Map
	 */
	public ListValueMap(Map<? extends K, ? extends Collection<V>> m) {
		this(DEFAULT_LOAD_FACTOR, m);
	}

	/**
	 * 构造
	 *
	 * @param loadFactor 加载因子
	 * @param m Map
	 */
	public ListValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
		this(m.size(), loadFactor);
		this.putAllValues(m);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 * @param loadFactor 加载因子
	 */
	public ListValueMap(int initialCapacity, float loadFactor) {
		super(new HashMap<>(initialCapacity, loadFactor));
	}
	// ------------------------------------------------------------------------- Constructor end

	@Override
	protected List<V> createCollection() {
		return new ArrayList<>(DEFAULT_COLLECTION_INITIAL_CAPACITY);
	}
}
