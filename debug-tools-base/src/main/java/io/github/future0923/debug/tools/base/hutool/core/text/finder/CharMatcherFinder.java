/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
