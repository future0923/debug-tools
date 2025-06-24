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
package io.github.future0923.debug.tools.base.hutool.core.lang.mutable;

import java.io.Serializable;

/**
 * 可变 {@code boolean} 类型
 *
 * @see Boolean
 * @since 3.0.1
 */
public class MutableBool implements Comparable<MutableBool>, Mutable<Boolean>, Serializable {
	private static final long serialVersionUID = 1L;

	private boolean value;

	/**
	 * 构造，默认值0
	 */
	public MutableBool() {
	}

	/**
	 * 构造
	 * @param value 值
	 */
	public MutableBool(final boolean value) {
		this.value = value;
	}

	/**
	 * 构造
	 * @param value String值
	 * @throws NumberFormatException 转为Boolean错误
	 */
	public MutableBool(final String value) throws NumberFormatException {
		this.value = Boolean.parseBoolean(value);
	}

	@Override
	public Boolean get() {
		return this.value;
	}

	/**
	 * 设置值
	 * @param value 值
	 */
	public void set(final boolean value) {
		this.value = value;
	}

	@Override
	public void set(final Boolean value) {
		this.value = value;
	}

	// -----------------------------------------------------------------------
	/**
	 * 相等需同时满足如下条件：
	 * <ol>
	 * 	<li>非空</li>
	 * 	<li>类型为 MutableBool</li>
	 * 	<li>值相等</li>
	 * </ol>
	 *
	 * @param obj 比对的对象
	 * @return 相同返回<code>true</code>，否则 {@code false}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof MutableBool) {
			return value == ((MutableBool) obj).value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
	}

	// -----------------------------------------------------------------------
	/**
	 * 比较
	 *
	 * @param other 其它 MutableBool 对象
	 * @return x==y返回0，x&lt;y返回-1，x&gt;y返回1
	 */
	@Override
	public int compareTo(final MutableBool other) {
		return Boolean.compare(this.value, other.value);
	}

	// -----------------------------------------------------------------------
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
