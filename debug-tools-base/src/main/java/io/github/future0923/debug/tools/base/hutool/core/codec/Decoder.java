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
package io.github.future0923.debug.tools.base.hutool.core.codec;

/**
 * 解码接口
 *
 * @param <T> 被解码的数据类型
 * @param <R> 解码后的数据类型
 * @author looly
 * @since 5.7.22
 */
public interface Decoder<T, R> {

	/**
	 * 执行解码
	 *
	 * @param encoded 被解码的数据
	 * @return 解码后的数据
	 */
	R decode(T encoded);
}
