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

import java.time.ZoneId;
import java.util.TimeZone;

/**
 * {@link ZoneId}和{@link TimeZone}相关封装
 *
 * @author looly
 * @since 5.7.15
 */
public class ZoneUtil {

	/**
	 * {@link ZoneId}转换为{@link TimeZone}，{@code null}则返回系统默认值
	 *
	 * @param zoneId {@link ZoneId}，{@code null}则返回系统默认值
	 * @return {@link TimeZone}
	 */
	public static TimeZone toTimeZone(ZoneId zoneId) {
		if (null == zoneId) {
			return TimeZone.getDefault();
		}

		return TimeZone.getTimeZone(zoneId);
	}

	/**
	 * {@link TimeZone}转换为{@link ZoneId}，{@code null}则返回系统默认值
	 *
	 * @param timeZone {@link TimeZone}，{@code null}则返回系统默认值
	 * @return {@link ZoneId}
	 */
	public static ZoneId toZoneId(TimeZone timeZone) {
		if (null == timeZone) {
			return ZoneId.systemDefault();
		}

		return timeZone.toZoneId();
	}
}
