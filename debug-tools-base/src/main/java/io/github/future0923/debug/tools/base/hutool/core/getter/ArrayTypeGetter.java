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

/**
 * 数组类型的Get接口
 * @author Looly
 *
 */
public interface ArrayTypeGetter {
	/*-------------------------- 数组类型 start -------------------------------*/

	/**
	 * 获取Object型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	String[] getObjs(String key);

	/**
	 * 获取String型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	String[] getStrs(String key);

	/**
	 * 获取Integer型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Integer[] getInts(String key);

	/**
	 * 获取Short型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Short[] getShorts(String key);

	/**
	 * 获取Boolean型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Boolean[] getBools(String key);

	/**
	 * 获取Long型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Long[] getLongs(String key);

	/**
	 * 获取Character型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Character[] getChars(String key);

	/**
	 * 获取Double型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Double[] getDoubles(String key);

	/**
	 * 获取Byte型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	Byte[] getBytes(String key);

	/**
	 * 获取BigInteger型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	BigInteger[] getBigIntegers(String key);

	/**
	 * 获取BigDecimal型属性值数组
	 *
	 * @param key 属性名
	 * @return 属性值列表
	 */
	BigDecimal[] getBigDecimals(String key);
	/*-------------------------- 数组类型 end -------------------------------*/
}
