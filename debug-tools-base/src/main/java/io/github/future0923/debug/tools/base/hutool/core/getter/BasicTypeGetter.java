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
package io.github.future0923.debug.tools.base.hutool.core.getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 基本类型的getter接口<br>
 * 提供一个统一的接口定义返回不同类型的值（基本类型）<br>
 *
 * @author Looly
 * @param <K> key类型
 */
public interface BasicTypeGetter<K> {
	/*-------------------------- 基本类型 start -------------------------------*/

	/**
	 * 获取Object属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Object getObj(K key);

	/**
	 * 获取字符串型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	String getStr(K key);

	/**
	 * 获取int型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Integer getInt(K key);

	/**
	 * 获取short型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Short getShort(K key);

	/**
	 * 获取boolean型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Boolean getBool(K key);

	/**
	 * 获取long型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Long getLong(K key);

	/**
	 * 获取char型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Character getChar(K key);

	/**
	 * 获取float型属性值<br>
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Float getFloat(K key);

	/**
	 * 获取double型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Double getDouble(K key);

	/**
	 * 获取byte型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	Byte getByte(K key);

	/**
	 * 获取BigDecimal型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	BigDecimal getBigDecimal(K key);

	/**
	 * 获取BigInteger型属性值
	 *
	 * @param key 属性名
	 * @return 属性值
	 */
	BigInteger getBigInteger(K key);

	/**
	 * 获得Enum类型的值
	 *
	 * @param <E> 枚举类型
	 * @param clazz Enum的Class
	 * @param key KEY
	 * @return Enum类型的值，无则返回Null
	 */
	<E extends Enum<E>> E getEnum(Class<E> clazz, K key);

	/**
	 * 获取Date类型值
	 *
	 * @param key 属性名
	 * @return Date类型属性值
	 */
	Date getDate(K key);
	/*-------------------------- 基本类型 end -------------------------------*/
}
