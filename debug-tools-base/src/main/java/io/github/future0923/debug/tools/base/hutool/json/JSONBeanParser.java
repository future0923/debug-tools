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
 * 实现此接口的类可以通过实现{@code parse(value)}方法来将JSON中的值解析为此对象的值
 *
 * @author Looly
 * @since 5.7.8
 * @param <T> 参数类型
 */
public interface JSONBeanParser<T> {

	/**
	 * value转Bean<br>
	 * 通过实现此接口，将JSON中的值填充到当前对象的字段值中，即对象自行实现JSON反序列化逻辑
	 *
	 * @param value 被解析的对象类型，可能为JSON或者普通String、Number等
	 */
	void parse(T value);
}
