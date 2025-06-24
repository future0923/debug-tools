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
package io.github.future0923.debug.tools.base.hutool.core.text.finder;

import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

/**
 * 固定长度查找器<br>
 * 给定一个长度，查找的位置为from + length，一般用于分段截取
 *
 * @since 5.7.14
 * @author looly
 */
public class LengthFinder extends TextFinder {
	private static final long serialVersionUID = 1L;

	private final int length;

	/**
	 * 构造
	 * @param length 长度
	 */
	public LengthFinder(int length) {
		Assert.isTrue(length > 0, "Length must be great than 0");
		this.length = length;
	}

	@Override
	public int start(int from) {
		Assert.notNull(this.text, "Text to find must be not null!");
		final int limit = getValidEndIndex();
		int result;
		if(negative){
			result = from - length;
			if(result > limit){
				return result;
			}
		} else {
			result = from + length;
			if(result < limit){
				return result;
			}
		}
		return -1;
	}

	@Override
	public int end(int start) {
		return start;
	}
}
