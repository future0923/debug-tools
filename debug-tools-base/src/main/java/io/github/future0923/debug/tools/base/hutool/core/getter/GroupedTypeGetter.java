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

/**
 * 基于分组的Get接口
 * @author Looly
 *
 */
public interface GroupedTypeGetter {
	/*-------------------------- 基本类型 start -------------------------------*/
	/**
	 * 获取字符串型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	String getStrByGroup(String key, String group);

	/**
	 * 获取int型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Integer getIntByGroup(String key, String group);

	/**
	 * 获取short型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Short getShortByGroup(String key, String group);

	/**
	 * 获取boolean型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Boolean getBoolByGroup(String key, String group);

	/**
	 * 获取Long型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Long getLongByGroup(String key, String group);

	/**
	 * 获取char型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Character getCharByGroup(String key, String group);

	/**
	 * 获取double型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Double getDoubleByGroup(String key, String group);

	/**
	 * 获取byte型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	Byte getByteByGroup(String key, String group);

	/**
	 * 获取BigDecimal型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	BigDecimal getBigDecimalByGroup(String key, String group);

	/**
	 * 获取BigInteger型属性值<br>
	 *
	 * @param key 属性名
	 * @param group 分组
	 * @return 属性值
	 */
	BigInteger getBigIntegerByGroup(String key, String group);
	/*-------------------------- 基本类型 end -------------------------------*/
}
