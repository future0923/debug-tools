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
package io.github.future0923.debug.tools.base.hutool.core.lang.mutable;

import io.github.future0923.debug.tools.base.hutool.core.lang.Pair;

/**
 * 可变{@link Pair}实现，可以修改键和值
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 5.7.16
 */
public class MutablePair<K, V> extends Pair<K, V> implements Mutable<Pair<K, V>>{
	private static final long serialVersionUID = 1L;

	/**
	 * 构造
	 *
	 * @param key   键
	 * @param value 值
	 */
	public MutablePair(K key, V value) {
		super(key, value);
	}

	/**
	 * 设置键
	 *
	 * @param key 新键
	 * @return this
	 */
	public MutablePair<K, V> setKey(K key) {
		this.key = key;
		return this;
	}

	/**
	 * 设置值
	 *
	 * @param value 新值
	 * @return this
	 */
	public MutablePair<K, V> setValue(V value) {
		this.value = value;
		return this;
	}

	@Override
	public Pair<K, V> get() {
		return this;
	}

	@Override
	public void set(Pair<K, V> pair) {
		this.key = pair.getKey();
		this.value = pair.getValue();
	}
}
