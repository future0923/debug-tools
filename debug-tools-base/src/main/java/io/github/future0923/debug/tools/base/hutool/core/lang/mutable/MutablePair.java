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
