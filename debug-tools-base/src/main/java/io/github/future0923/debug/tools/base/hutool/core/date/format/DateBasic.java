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
package io.github.future0923.debug.tools.base.hutool.core.date.format;

import java.util.Locale;
import java.util.TimeZone;

/**
 * 日期基本信息获取接口
 *
 * @author Looly
 * @since 2.16.2
 */
public interface DateBasic {

	/**
	 * 获得日期格式化或者转换的格式
	 *
	 * @return {@link java.text.SimpleDateFormat}兼容的格式
	 */
	String getPattern();

	/**
	 * 获得时区
	 *
	 * @return {@link TimeZone}
	 */
	TimeZone getTimeZone();

	/**
	 * 获得 日期地理位置
	 *
	 * @return {@link Locale}
	 */
	Locale getLocale();
}
