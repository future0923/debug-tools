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

/**
 * {@code JSONString}接口定义了一个{@code toJSONString()}<br>
 * 实现此接口的类可以通过实现{@code toJSONString()}方法来改变转JSON字符串的方式。
 *
 * @author Looly
 *
 */
public interface JSONString {

	/**
	 * 自定义转JSON字符串的方法
	 *
	 * @return JSON字符串
	 */
	String toJSONString();
}
