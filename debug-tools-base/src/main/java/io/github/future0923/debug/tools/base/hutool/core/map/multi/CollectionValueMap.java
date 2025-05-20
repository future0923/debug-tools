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
package io.github.future0923.debug.tools.base.hutool.core.map.multi;

import io.github.future0923.debug.tools.base.hutool.core.lang.func.Func0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 值作为集合的Map实现，通过调用putValue可以在相同key时加入多个值，多个值用集合表示<br>
 * 此类可以通过传入函数自定义集合类型的创建规则
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author looly
 * @since 4.3.3
 */
public class CollectionValueMap<K, V> extends AbsCollValueMap<K, V, Collection<V>> {
	private static final long serialVersionUID = 9012989578038102983L;

	private final Func0<Collection<V>> collectionCreateFunc;

	// ------------------------------------------------------------------------- Constructor start

	/**
	 * 构造
	 */
	public CollectionValueMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 */
	public CollectionValueMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * 构造
	 *
	 * @param m Map
	 */
	public CollectionValueMap(Map<? extends K, ? extends Collection<V>> m) {
		this(DEFAULT_LOAD_FACTOR, m);
	}

	/**
	 * 构造
	 *
	 * @param loadFactor 加载因子
	 * @param m          Map
	 */
	public CollectionValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m) {
		this(loadFactor, m, ArrayList::new);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 * @param loadFactor      加载因子
	 */
	public CollectionValueMap(int initialCapacity, float loadFactor) {
		this(initialCapacity, loadFactor, ArrayList::new);
	}

	/**
	 * 构造
	 *
	 * @param loadFactor           加载因子
	 * @param m                    Map
	 * @param collectionCreateFunc Map中值的集合创建函数
	 * @since 5.7.4
	 */
	public CollectionValueMap(float loadFactor, Map<? extends K, ? extends Collection<V>> m, Func0<Collection<V>> collectionCreateFunc) {
		this(m.size(), loadFactor, collectionCreateFunc);
		this.putAll(m);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity      初始大小
	 * @param loadFactor           加载因子
	 * @param collectionCreateFunc Map中值的集合创建函数
	 * @since 5.7.4
	 */
	public CollectionValueMap(int initialCapacity, float loadFactor, Func0<Collection<V>> collectionCreateFunc) {
		super(new HashMap<>(initialCapacity, loadFactor));
		this.collectionCreateFunc = collectionCreateFunc;
	}
	// ------------------------------------------------------------------------- Constructor end

	@Override
	protected Collection<V> createCollection() {
		return collectionCreateFunc.callWithRuntimeException();
	}
}
