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
package io.github.future0923.debug.tools.base.hutool.core.comparator;

import java.util.Comparator;

/**
 * 字符串长度比较器，短在前
 *
 * @author looly
 * @since 5.8.9
 */
public class LengthComparator implements Comparator<CharSequence> {
	/**
	 * 单例的字符串长度比较器，短在前
	 */
	public static final LengthComparator INSTANCE = new LengthComparator();

	@Override
	public int compare(CharSequence o1, CharSequence o2) {
		int result = Integer.compare(o1.length(), o2.length());
		if (0 == result) {
			result = CompareUtil.compare(o1.toString(), o2.toString());
		}
		return result;
	}
}
