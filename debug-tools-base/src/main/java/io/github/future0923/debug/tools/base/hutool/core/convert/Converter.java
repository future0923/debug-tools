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

/**
 * 转换器接口，实现类型转换
 *
 * @param <T> 转换到的目标类型
 * @author Looly
 */
public interface Converter<T> {

	/**
	 * 转换为指定类型<br>
	 * 如果类型无法确定，将读取默认值的类型做为目标类型
	 *
	 * @param value 原始值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 * @throws IllegalArgumentException 无法确定目标类型，且默认值为{@code null}，无法确定类型
	 */
	T convert(Object value, T defaultValue) throws IllegalArgumentException;

	/**
	 * 转换值为指定类型，可选是否不抛异常转换<br>
	 * 当转换失败时返回默认值
	 *
	 * @param value 值
	 * @param defaultValue 默认值
	 * @param quietly 是否静默转换，true不抛异常
	 * @return 转换后的值
	 * @since 5.8.0
	 * @see #convert(Object, Object)
	 */
	default T convertWithCheck(Object value, T defaultValue, boolean quietly) {
		try {
			return convert(value, defaultValue);
		} catch (Exception e) {
			if(quietly){
				return defaultValue;
			}
			throw e;
		}
	}
}
