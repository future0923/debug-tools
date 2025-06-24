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
import io.github.future0923.debug.tools.base.hutool.core.lang.Matcher;

/**
 * 字符匹配查找器<br>
 * 查找满足指定{@link Matcher} 匹配的字符所在位置，此类长用于查找某一类字符，如数字等
 *
 * @since 5.7.14
 * @author looly
 */
public class CharMatcherFinder extends TextFinder {
	private static final long serialVersionUID = 1L;

	private final Matcher<Character> matcher;

	/**
	 * 构造
	 * @param matcher 被查找的字符匹配器
	 */
	public CharMatcherFinder(Matcher<Character> matcher) {
		this.matcher = matcher;
	}

	@Override
	public int start(int from) {
		Assert.notNull(this.text, "Text to find must be not null!");
		final int limit = getValidEndIndex();
		if(negative){
			for (int i = from; i > limit; i--) {
				if(matcher.match(text.charAt(i))){
					return i;
				}
			}
		} else {
			for (int i = from; i < limit; i++) {
				if(matcher.match(text.charAt(i))){
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public int end(int start) {
		if(start < 0){
			return -1;
		}
		return start + 1;
	}
}
