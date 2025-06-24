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


import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

public abstract class AbstractDateBasic implements DateBasic, Serializable {
	private static final long serialVersionUID = 6333136319870641818L;

	/** The pattern */
	protected final  String pattern;
	/** The time zone. */
	protected final TimeZone timeZone;
	/** The locale. */
	protected final Locale locale;

	/**
	 * 构造，内部使用
	 * @param pattern 使用{@link java.text.SimpleDateFormat} 相同的日期格式
	 * @param timeZone 非空时区{@link TimeZone}
	 * @param locale 非空{@link Locale} 日期地理位置
	 */
	protected AbstractDateBasic(final String pattern, final TimeZone timeZone, final Locale locale) {
		this.pattern = pattern;
		this.timeZone = timeZone;
		this.locale = locale;
	}

	// ----------------------------------------------------------------------- Accessors
	@Override
	public String getPattern() {
		return pattern;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	// ----------------------------------------------------------------------- Basics
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof FastDatePrinter == false) {
			return false;
		}
		final AbstractDateBasic other = (AbstractDateBasic) obj;
		return pattern.equals(other.pattern) && timeZone.equals(other.timeZone) && locale.equals(other.locale);
	}

	@Override
	public int hashCode() {
		return pattern.hashCode() + 13 * (timeZone.hashCode() + 13 * locale.hashCode());
	}

	@Override
	public String toString() {
		return "FastDatePrinter[" + pattern + "," + locale + "," + timeZone.getID() + "]";
	}
}
