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


import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 自定义键值函数风格的Map
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Looly
 * @since 5.8.0
 */
public class FuncMap<K, V> extends TransMap<K, V> {
	private static final long serialVersionUID = 1L;

	private final Function<Object, K> keyFunc;
	private final Function<Object, V> valueFunc;

	// ------------------------------------------------------------------------- Constructor start

	/**
	 * 构造<br>
	 * 注意提供的Map中不能有键值对，否则可能导致自定义key失效
	 *
	 * @param mapFactory  Map，提供的空map
	 * @param keyFunc   自定义KEY的函数
	 * @param valueFunc 自定义value函数
	 */
	public FuncMap(Supplier<Map<K, V>> mapFactory, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
		this(mapFactory.get(), keyFunc, valueFunc);
	}

	/**
	 * 构造<br>
	 * 注意提供的Map中不能有键值对，否则可能导致自定义key失效
	 *
	 * @param emptyMap  Map，提供的空map
	 * @param keyFunc   自定义KEY的函数
	 * @param valueFunc 自定义value函数
	 */
	public FuncMap(Map<K, V> emptyMap, Function<Object, K> keyFunc, Function<Object, V> valueFunc) {
		super(emptyMap);
		this.keyFunc = keyFunc;
		this.valueFunc = valueFunc;
	}
	// ------------------------------------------------------------------------- Constructor end

	/**
	 * 根据函数自定义键
	 *
	 * @param key KEY
	 * @return 驼峰Key
	 */
	@Override
	protected K customKey(Object key) {
		if (null != this.keyFunc) {
			return keyFunc.apply(key);
		}
		//noinspection unchecked
		return (K) key;
	}

	@Override
	protected V customValue(Object value) {
		if (null != this.valueFunc) {
			return valueFunc.apply(value);
		}
		//noinspection unchecked
		return (V) value;
	}
}
