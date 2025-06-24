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

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * 按照GBK拼音顺序对给定的汉字字符串排序
 *
 * @author looly
 * @since 4.0.8
 */
public class PinyinComparator implements Comparator<String>, Serializable {
	private static final long serialVersionUID = 1L;

	final Collator collator;

	/**
	 * 构造
	 */
	public PinyinComparator() {
		collator = Collator.getInstance(Locale.CHINESE);
	}

	@Override
	public int compare(String o1, String o2) {
		return collator.compare(o1, o2);
	}

}
