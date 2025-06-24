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
package io.github.future0923.debug.tools.base.hutool.core.text;

/**
 * ASCII字符对应的字符串缓存
 *
 * @author looly
 * @since 4.0.1
 *
 */
public class ASCIIStrCache {

	private static final int ASCII_LENGTH = 128;
	private static final String[] CACHE = new String[ASCII_LENGTH];
	static {
		for (char c = 0; c < ASCII_LENGTH; c++) {
			CACHE[c] = String.valueOf(c);
		}
	}

	/**
	 * 字符转为字符串<br>
	 * 如果为ASCII字符，使用缓存
	 *
	 * @param c 字符
	 * @return 字符串
	 */
	public static String toString(char c) {
		return c < ASCII_LENGTH ? CACHE[c] : String.valueOf(c);
	}
}
