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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 忽略大小写的Map<br>
 * 对KEY忽略大小写，get("Value")和get("value")获得的值相同，put进入的值也会被覆盖
 *
 * @author Looly
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @since 3.0.2
 */
public class CaseInsensitiveMap<K, V> extends FuncKeyMap<K, V> {
	private static final long serialVersionUID = 4043263744224569870L;

	//------------------------------------------------------------------------- Constructor start
	/**
	 * 构造
	 */
	public CaseInsensitiveMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 */
	public CaseInsensitiveMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * 构造<br>
	 * 注意此构造将传入的Map所有值复制到当前map中，不修改传入map。
	 *
	 * @param m 被包装的自定义Map创建器
	 */
	public CaseInsensitiveMap(Map<? extends K, ? extends V> m) {
		this(DEFAULT_LOAD_FACTOR, m);
	}

	/**
	 * 构造
	 *
	 * @param loadFactor 加载因子
	 * @param m Map
	 * @since 3.1.2
	 */
	public CaseInsensitiveMap(float loadFactor, Map<? extends K, ? extends V> m) {
		this(m.size(), loadFactor);
		this.putAll(m);
	}

	/**
	 * 构造
	 *
	 * @param initialCapacity 初始大小
	 * @param loadFactor 加载因子
	 */
	public CaseInsensitiveMap(int initialCapacity, float loadFactor) {
		this(MapBuilder.create(new HashMap<>(initialCapacity, loadFactor)));
	}

	/**
	 * 构造<br>
	 * 注意此构造将传入的Map作为被包装的Map，针对任何修改，传入的Map都会被同样修改。
	 *
	 * @param emptyMapBuilder 被包装的自定义Map创建器
	 */
	CaseInsensitiveMap(MapBuilder<K, V> emptyMapBuilder) {
		// issue#I5VRHW@Gitee 使Function可以被序列化
		super(emptyMapBuilder.build(), (Function<Object, K> & Serializable)(key)->{
			if (key instanceof CharSequence) {
				key = key.toString().toLowerCase();
			}
			//noinspection unchecked
			return (K) key;
		});
	}
	//------------------------------------------------------------------------- Constructor end
}
