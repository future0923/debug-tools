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

import io.github.future0923.debug.tools.base.hutool.core.convert.impl.DateConverter;
import io.github.future0923.debug.tools.base.hutool.core.convert.impl.TemporalAccessorConverter;

import java.lang.reflect.Type;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 包含格式的数字转换器，主要针对带格式的时间戳
 *
 * @author looly
 * @since 5.8.13
 */
public class NumberWithFormat extends Number implements TypeConverter {
	private static final long serialVersionUID = 1L;

	private final Number number;
	private final String format;

	/**
	 * 构造
	 *
	 * @param number 数字
	 * @param format 格式
	 */
	public NumberWithFormat(final Number number, final String format) {
		this.number = number;
		this.format = format;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Type targetType, Object value) {
		// 自定义日期格式
		if (null != this.format && targetType instanceof Class) {
			final Class<?> clazz = (Class<?>) targetType;
			// https://gitee.com/chinabugotech/hutool/issues/I6IS5B
			if (Date.class.isAssignableFrom(clazz)) {
				return new DateConverter((Class<? extends Date>) clazz, format).convert(this.number, null);
			} else if (TemporalAccessor.class.isAssignableFrom(clazz)) {
				return new TemporalAccessorConverter(clazz, format).convert(this.number, null);
			} else if (String.class == clazz) {
				return toString();
			}

			// 其他情况按照正常数字转换
		}

		// 按照正常数字转换
		return Convert.convertWithCheck(targetType, this.number, null, false);
	}

	/**
	 * 获取原始Number
	 *
	 * @return 原始Number
	 * @since 5.8.32
	 */
	public Object getNumber() {
		return this.number;
	}

	@Override
	public int intValue() {
		return this.number.intValue();
	}

	@Override
	public long longValue() {
		return this.number.longValue();
	}

	@Override
	public float floatValue() {
		return this.number.floatValue();
	}

	@Override
	public double doubleValue() {
		return this.number.doubleValue();
	}

	@Override
	public String toString() {
		return this.number.toString();
	}
}
