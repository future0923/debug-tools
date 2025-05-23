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
