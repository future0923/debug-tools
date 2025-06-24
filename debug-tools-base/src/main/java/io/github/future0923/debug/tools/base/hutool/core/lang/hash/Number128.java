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
package io.github.future0923.debug.tools.base.hutool.core.lang.hash;

import java.util.Objects;

/**
 * 128位数字表示，分高位和低位
 *
 * @author hexiufeng
 * @since 5.2.5
 */
public class Number128 extends Number {
	private static final long serialVersionUID = 1L;

	private long lowValue;
	private long highValue;

	/**
	 * 构造
	 *
	 * @param lowValue  低位
	 * @param highValue 高位
	 */
	public Number128(long lowValue, long highValue) {
		this.lowValue = lowValue;
		this.highValue = highValue;
	}

	/**
	 * 获取低位值
	 *
	 * @return 地位值
	 */
	public long getLowValue() {
		return lowValue;
	}

	/**
	 * 设置低位值
	 *
	 * @param lowValue 低位值
	 */
	public void setLowValue(long lowValue) {
		this.lowValue = lowValue;
	}

	/**
	 * 获取高位值
	 *
	 * @return 高位值
	 */
	public long getHighValue() {
		return highValue;
	}

	/**
	 * 设置高位值
	 *
	 * @param hiValue 高位值
	 */
	public void setHighValue(long hiValue) {
		this.highValue = hiValue;
	}

	/**
	 * 获取高低位数组，long[0]：低位，long[1]：高位
	 *
	 * @return 高低位数组，long[0]：低位，long[1]：高位
	 */
	public long[] getLongArray() {
		return new long[]{lowValue, highValue};
	}

	@Override
	public int intValue() {
		return (int) longValue();
	}

	@Override
	public long longValue() {
		return this.lowValue;
	}

	@Override
	public float floatValue() {
		return longValue();
	}

	@Override
	public double doubleValue() {
		return longValue();
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final Number128 number128 = (Number128) o;
		return lowValue == number128.lowValue && highValue == number128.highValue;
	}

	@Override
	public int hashCode() {
		return Objects.hash(lowValue, highValue);
	}
}
