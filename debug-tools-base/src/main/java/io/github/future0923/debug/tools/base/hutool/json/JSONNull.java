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
package io.github.future0923.debug.tools.base.hutool.json;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * 用于定义{@code null}，与Javascript中null相对应<br>
 * Java中的{@code null}值在js中表示为undefined。
 *
 * @author Looly
 */
public class JSONNull implements Serializable {
	private static final long serialVersionUID = 2633815155870764938L;

	/**
	 * {@code NULL} 对象用于减少歧义来表示Java 中的{@code null} <br>
	 * {@code NULL.equals(null)} 返回 {@code true}. <br>
	 * {@code NULL.toString()} 返回 {@code "null"}.
	 */
	public static final JSONNull NULL = new JSONNull();

	/**
	 * A Null object is equal to the null value and to itself.
	 * 对象与其本身和{@code null}值相等
	 *
	 * @param object An object to test for nullness.
	 * @return true if the object parameter is the JSONObject.NULL object or null.
	 */
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	@Override
	public boolean equals(Object object) {
		return object == null || (object == this);
	}

	/**
	 * Get the "null" string value.
	 * 获得“null”字符串
	 *
	 * @return The string "null".
	 */
	@Override
	public String toString() {
		return StrUtil.NULL;
	}
}
