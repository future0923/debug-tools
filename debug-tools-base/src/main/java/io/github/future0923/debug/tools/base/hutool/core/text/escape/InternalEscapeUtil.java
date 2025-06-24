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
package io.github.future0923.debug.tools.base.hutool.core.text.escape;

/**
 * 内部Escape工具类
 * @author looly
 *
 */
class InternalEscapeUtil {

	/**
	 * 将数组中的0和1位置的值互换，即键值转换
	 *
	 * @param array String[][] 被转换的数组
	 * @return String[][] 转换后的数组
	 */
	public static String[][] invert(final String[][] array) {
		final String[][] newarray = new String[array.length][2];
		for (int i = 0; i < array.length; i++) {
			newarray[i][0] = array[i][1];
			newarray[i][1] = array[i][0];
		}
		return newarray;
	}
}
