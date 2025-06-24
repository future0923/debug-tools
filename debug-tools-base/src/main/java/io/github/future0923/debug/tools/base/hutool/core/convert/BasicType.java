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
package io.github.future0923.debug.tools.base.hutool.core.convert;

import io.github.future0923.debug.tools.base.hutool.core.map.SafeConcurrentHashMap;

import java.util.Map;

/**
 * 基本变量类型的枚举<br>
 * 基本类型枚举包括原始类型和包装类型
 * @author xiaoleilu
 */
public enum BasicType {
	BYTE, SHORT, INT, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, CHAR, CHARACTER, STRING;

	/** 包装类型为Key，原始类型为Value，例如： Integer.class =》 int.class. */
	public static final Map<Class<?>, Class<?>> WRAPPER_PRIMITIVE_MAP = new SafeConcurrentHashMap<>(8);
	/** 原始类型为Key，包装类型为Value，例如： int.class =》 Integer.class. */
	public static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new SafeConcurrentHashMap<>(8);

	static {
		WRAPPER_PRIMITIVE_MAP.put(Boolean.class, boolean.class);
		WRAPPER_PRIMITIVE_MAP.put(Byte.class, byte.class);
		WRAPPER_PRIMITIVE_MAP.put(Character.class, char.class);
		WRAPPER_PRIMITIVE_MAP.put(Double.class, double.class);
		WRAPPER_PRIMITIVE_MAP.put(Float.class, float.class);
		WRAPPER_PRIMITIVE_MAP.put(Integer.class, int.class);
		WRAPPER_PRIMITIVE_MAP.put(Long.class, long.class);
		WRAPPER_PRIMITIVE_MAP.put(Short.class, short.class);

		for (Map.Entry<Class<?>, Class<?>> entry : WRAPPER_PRIMITIVE_MAP.entrySet()) {
			PRIMITIVE_WRAPPER_MAP.put(entry.getValue(), entry.getKey());
		}
	}

	/**
	 * 原始类转为包装类，非原始类返回原类
	 * @param clazz 原始类
	 * @return 包装类
	 */
	public static Class<?> wrap(Class<?> clazz){
		if(null == clazz || false == clazz.isPrimitive()){
			return clazz;
		}
		Class<?> result = PRIMITIVE_WRAPPER_MAP.get(clazz);
		return (null == result) ? clazz : result;
	}

	/**
	 * 包装类转为原始类，非包装类返回原类
	 * @param clazz 包装类
	 * @return 原始类
	 */
	public static Class<?> unWrap(Class<?> clazz){
		if(null == clazz || clazz.isPrimitive()){
			return clazz;
		}
		Class<?> result = WRAPPER_PRIMITIVE_MAP.get(clazz);
		return (null == result) ? clazz : result;
	}
}
