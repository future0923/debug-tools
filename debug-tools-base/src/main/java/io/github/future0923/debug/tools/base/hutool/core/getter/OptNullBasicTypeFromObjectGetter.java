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
package io.github.future0923.debug.tools.base.hutool.core.getter;

import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 基本类型的getter接口抽象实现，所有类型的值获取都是通过将getObj获得的值转换而来<br>
 * 用户只需实现getObj方法即可，其他类型将会从Object结果中转换
 * 在不提供默认值的情况下， 如果值不存在或获取错误，返回null<br>
 *
 * @author Looly
 */
public interface OptNullBasicTypeFromObjectGetter<K> extends OptNullBasicTypeGetter<K> {
	@Override
	default String getStr(K key, String defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toStr(obj, defaultValue);
	}

	@Override
	default Integer getInt(K key, Integer defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toInt(obj, defaultValue);
	}

	@Override
	default Short getShort(K key, Short defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toShort(obj, defaultValue);
	}

	@Override
	default Boolean getBool(K key, Boolean defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toBool(obj, defaultValue);
	}

	@Override
	default Long getLong(K key, Long defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toLong(obj, defaultValue);
	}

	@Override
	default Character getChar(K key, Character defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toChar(obj, defaultValue);
	}

	@Override
	default Float getFloat(K key, Float defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toFloat(obj, defaultValue);
	}

	@Override
	default Double getDouble(K key, Double defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toDouble(obj, defaultValue);
	}

	@Override
	default Byte getByte(K key, Byte defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toByte(obj, defaultValue);
	}

	@Override
	default BigDecimal getBigDecimal(K key, BigDecimal defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toBigDecimal(obj, defaultValue);
	}

	@Override
	default BigInteger getBigInteger(K key, BigInteger defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toBigInteger(obj, defaultValue);
	}

	@Override
	default <E extends Enum<E>> E getEnum(Class<E> clazz, K key, E defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toEnum(clazz, obj, defaultValue);
	}

	@Override
	default Date getDate(K key, Date defaultValue) {
		final Object obj = getObj(key);
		if (null == obj) {
			return defaultValue;
		}
		return Convert.toDate(obj, defaultValue);
	}
}
