/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.base.hutool.core.date;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;
import io.github.future0923.debug.tools.base.hutool.core.util.ArrayUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.time.DayOfWeek;
import java.util.Calendar;

/**
 * 星期枚举<br>
 * 与Calendar中的星期int值对应
 *
 * @author Looly
 * @see #SUNDAY
 * @see #MONDAY
 * @see #TUESDAY
 * @see #WEDNESDAY
 * @see #THURSDAY
 * @see #FRIDAY
 * @see #SATURDAY
 */
public enum Week {

	/**
	 * 周日
	 */
	SUNDAY(Calendar.SUNDAY),
	/**
	 * 周一
	 */
	MONDAY(Calendar.MONDAY),
	/**
	 * 周二
	 */
	TUESDAY(Calendar.TUESDAY),
	/**
	 * 周三
	 */
	WEDNESDAY(Calendar.WEDNESDAY),
	/**
	 * 周四
	 */
	THURSDAY(Calendar.THURSDAY),
	/**
	 * 周五
	 */
	FRIDAY(Calendar.FRIDAY),
	/**
	 * 周六
	 */
	SATURDAY(Calendar.SATURDAY);

	// ---------------------------------------------------------------
	/**
	 * Weeks aliases.
	 */
	private static final String[] ALIASES = {"sun", "mon", "tue", "wed", "thu", "fri", "sat"};
	private static final Week[] ENUMS = Week.values();

	/**
	 * 星期对应{@link Calendar} 中的Week值
	 */
	private final int value;

	/**
	 * 构造
	 *
	 * @param value 星期对应{@link Calendar} 中的Week值
	 */
	Week(int value) {
		this.value = value;
	}

	/**
	 * 获得星期对应{@link Calendar} 中的Week值
	 *
	 * @return 星期对应 {@link Calendar} 中的Week值
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * 获取ISO8601规范的int值，from 1 (Monday) to 7 (Sunday).
	 *
	 * @return ISO8601规范的int值
	 * @since 5.8.0
	 */
	public int getIso8601Value() {
		int iso8601IntValue = getValue() - 1;
		if (0 == iso8601IntValue) {
			iso8601IntValue = 7;
		}
		return iso8601IntValue;
	}

	/**
	 * 转换为中文名
	 *
	 * @return 星期的中文名
	 * @since 3.3.0
	 */
	public String toChinese() {
		return toChinese("星期");
	}

	/**
	 * 转换为中文名
	 *
	 * @param weekNamePre 表示星期的前缀，例如前缀为“星期”，则返回结果为“星期一”；前缀为”周“，结果为“周一”
	 * @return 星期的中文名
	 * @since 4.0.11
	 */
	public String toChinese(String weekNamePre) {
		switch (this) {
			case SUNDAY:
				return weekNamePre + "日";
			case MONDAY:
				return weekNamePre + "一";
			case TUESDAY:
				return weekNamePre + "二";
			case WEDNESDAY:
				return weekNamePre + "三";
			case THURSDAY:
				return weekNamePre + "四";
			case FRIDAY:
				return weekNamePre + "五";
			case SATURDAY:
				return weekNamePre + "六";
			default:
				return null;
		}
	}

	/**
	 * 转换为{@link DayOfWeek}
	 *
	 * @return {@link DayOfWeek}
	 * @since 5.8.0
	 */
	public DayOfWeek toJdkDayOfWeek() {
		return DayOfWeek.of(getIso8601Value());
	}

	/**
	 * 将 {@link Calendar}星期相关值转换为Week枚举对象<br>
	 *
	 * @param calendarWeekIntValue Calendar中关于Week的int值，1表示Sunday
	 * @return Week
	 * @see #SUNDAY
	 * @see #MONDAY
	 * @see #TUESDAY
	 * @see #WEDNESDAY
	 * @see #THURSDAY
	 * @see #FRIDAY
	 * @see #SATURDAY
	 */
	public static Week of(int calendarWeekIntValue) {
		if (calendarWeekIntValue > ENUMS.length || calendarWeekIntValue < 1) {
			return null;
		}
		return ENUMS[calendarWeekIntValue - 1];
	}

	/**
	 * 解析别名为Week对象，别名如：sun或者SUNDAY，不区分大小写
	 *
	 * @param name 别名值
	 * @return 周枚举Week，非空
	 * @throws IllegalArgumentException 如果别名无对应的枚举，抛出此异常
	 * @since 5.8.0
	 */
	public static Week of(String name) throws IllegalArgumentException {
		Assert.notBlank(name);

		// issue#3637
		if (StrUtil.startWithAny(name, "星期", "周")) {
			char chineseNumber = name.charAt(name.length() - 1);
			switch (chineseNumber) {
				case '一':
					return MONDAY;
				case '二':
					return TUESDAY;
				case '三':
					return WEDNESDAY;
				case '四':
					return THURSDAY;
				case '五':
					return FRIDAY;
				case '六':
					return SATURDAY;
				case '日':
					return SUNDAY;
			}
			throw new IllegalArgumentException("Invalid week name: " + name);
		}

		Week of = of(ArrayUtil.indexOfIgnoreCase(ALIASES, name) + 1);
		if (null == of) {
			of = Week.valueOf(name.toUpperCase());
		}
		return of;
	}

	/**
	 * 将 {@link DayOfWeek}星期相关值转换为Week枚举对象<br>
	 *
	 * @param dayOfWeek DayOfWeek星期值
	 * @return Week
	 * @see #SUNDAY
	 * @see #MONDAY
	 * @see #TUESDAY
	 * @see #WEDNESDAY
	 * @see #THURSDAY
	 * @see #FRIDAY
	 * @see #SATURDAY
	 * @since 5.7.14
	 */
	public static Week of(DayOfWeek dayOfWeek) {
		Assert.notNull(dayOfWeek);
		int week = dayOfWeek.getValue() + 1;
		if (8 == week) {
			// 周日
			week = 1;
		}
		return of(week);
	}
}
