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
package io.github.future0923.debug.tools.base.hutool.core.date.chinese;


/**
 * 农历月份表示
 *
 * @author looly
 * @since 5.4.1
 */
public class ChineseMonth {

	private static final String[] MONTH_NAME = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
	private static final String[] MONTH_NAME_TRADITIONAL = {"正", "二", "三", "四", "五", "六", "七", "八", "九", "寒", "冬", "腊"};

	/**
	 * 当前农历月份是否为闰月
	 *
	 * @param year  农历年
	 * @param month 农历月
	 * @return 是否为闰月
	 * @since 5.4.2
	 */
	public static boolean isLeapMonth(int year, int month) {
		return month == LunarInfo.leapMonth(year);
	}

	/**
	 * 获得农历月称呼<br>
	 * 当为传统表示时，表示为二月，腊月，或者润正月等
	 * 当为非传统表示时，二月，十二月，或者润一月等
	 *
	 * @param isLeapMonth   是否闰月
	 * @param month         月份，从1开始，如果是闰月，应传入需要显示的月份
	 * @param isTraditional 是否传统表示，例如一月传统表示为正月
	 * @return 返回农历月份称呼
	 */
	public static String getChineseMonthName(boolean isLeapMonth, int month, boolean isTraditional) {
		return (isLeapMonth ? "闰" : "") + (isTraditional ? MONTH_NAME_TRADITIONAL : MONTH_NAME)[month - 1] + "月";
	}
}
