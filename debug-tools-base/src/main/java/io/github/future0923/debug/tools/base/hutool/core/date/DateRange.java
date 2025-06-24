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
package io.github.future0923.debug.tools.base.hutool.core.date;

import io.github.future0923.debug.tools.base.hutool.core.lang.Range;

import java.util.Date;

/**
 * 日期范围
 *
 * @author looly
 * @since 4.1.0
 */
public class DateRange extends Range<DateTime> {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造，包含开始和结束日期时间
	 *
	 * @param start 起始日期时间（包括）
	 * @param end 结束日期时间（包括）
	 * @param unit 步进单位
	 */
	public DateRange(Date start, Date end, DateField unit) {
		this(start, end, unit, 1);
	}

	/**
	 * 构造，包含开始和结束日期时间
	 *
	 * @param start 起始日期时间（包括）
	 * @param end 结束日期时间（包括）
	 * @param unit 步进单位
	 * @param step 步进数
	 */
	public DateRange(Date start, Date end, DateField unit, int step) {
		this(start, end, unit, step, true, true);
	}

	/**
	 * 构造
	 *
	 * @param start 起始日期时间
	 * @param end 结束日期时间
	 * @param unit 步进单位
	 * @param step 步进数
	 * @param isIncludeStart 是否包含开始的时间
	 * @param isIncludeEnd 是否包含结束的时间
	 */
	public DateRange(Date start, Date end, DateField unit, int step, boolean isIncludeStart, boolean isIncludeEnd) {
		super(DateUtil.date(start), DateUtil.date(end), (current, end1, index) -> {
			if(step <= 0){
				// issue#3783
				return null;
			}
			final DateTime dt = DateUtil.date(start).offsetNew(unit, (index + 1) * step);
			if (dt.isAfter(end1)) {
				return null;
			}
			return dt;
		}, isIncludeStart, isIncludeEnd);
	}

}
