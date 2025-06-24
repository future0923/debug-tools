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

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

/**
 * 计时器<br>
 * 计算某个过程花费的时间，精确到毫秒或纳秒
 *
 * @author Looly
 */
public class TimeInterval extends GroupTimeInterval {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_ID = StrUtil.EMPTY;

	/**
	 * 构造，默认使用毫秒计数
	 */
	public TimeInterval() {
		this(false);
	}

	/**
	 * 构造
	 *
	 * @param isNano 是否使用纳秒计数，false则使用毫秒
	 */
	public TimeInterval(boolean isNano) {
		super(isNano);
		start();
	}

	/**
	 * @return 开始计时并返回当前时间
	 */
	public long start() {
		return start(DEFAULT_ID);
	}

	/**
	 * @return 重新计时并返回从开始到当前的持续时间
	 */
	public long intervalRestart() {
		return intervalRestart(DEFAULT_ID);
	}

	/**
	 * 重新开始计算时间（重置开始时间）
	 *
	 * @return this
	 * @see #start()
	 * @since 3.0.1
	 */
	public TimeInterval restart() {
		start(DEFAULT_ID);
		return this;
	}

	//----------------------------------------------------------- Interval

	/**
	 * 从开始到当前的间隔时间（毫秒数）<br>
	 * 如果使用纳秒计时，返回纳秒差，否则返回毫秒差
	 *
	 * @return 从开始到当前的间隔时间（毫秒数）
	 */
	public long interval() {
		return interval(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔时间（毫秒数），返回XX天XX小时XX分XX秒XX毫秒
	 *
	 * @return 从开始到当前的间隔时间（毫秒数）
	 * @since 4.6.7
	 */
	public String intervalPretty() {
		return intervalPretty(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔时间（毫秒数）
	 *
	 * @return 从开始到当前的间隔时间（毫秒数）
	 */
	public long intervalMs() {
		return intervalMs(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔秒数，取绝对值
	 *
	 * @return 从开始到当前的间隔秒数，取绝对值
	 */
	public long intervalSecond() {
		return intervalSecond(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔分钟数，取绝对值
	 *
	 * @return 从开始到当前的间隔分钟数，取绝对值
	 */
	public long intervalMinute() {
		return intervalMinute(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔小时数，取绝对值
	 *
	 * @return 从开始到当前的间隔小时数，取绝对值
	 */
	public long intervalHour() {
		return intervalHour(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔天数，取绝对值
	 *
	 * @return 从开始到当前的间隔天数，取绝对值
	 */
	public long intervalDay() {
		return intervalDay(DEFAULT_ID);
	}

	/**
	 * 从开始到当前的间隔周数，取绝对值
	 *
	 * @return 从开始到当前的间隔周数，取绝对值
	 */
	public long intervalWeek() {
		return intervalWeek(DEFAULT_ID);
	}
}
