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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则查找器<br>
 * 通过传入正则表达式，查找指定字符串中匹配正则的开始和结束位置
 *
 * @author looly
 * @since 5.7.14
 */
public class PatternFinder extends TextFinder {
	private static final long serialVersionUID = 1L;

	private final Pattern pattern;
	private Matcher matcher;

	/**
	 * 构造
	 *
	 * @param regex           被查找的正则表达式
	 * @param caseInsensitive 是否忽略大小写
	 */
	public PatternFinder(String regex, boolean caseInsensitive) {
		this(Pattern.compile(regex, caseInsensitive ? Pattern.CASE_INSENSITIVE : 0));
	}

	/**
	 * 构造
	 *
	 * @param pattern 被查找的正则{@link Pattern}
	 */
	public PatternFinder(Pattern pattern) {
		this.pattern = pattern;
	}

	@Override
	public TextFinder setText(CharSequence text) {
		this.matcher = pattern.matcher(text);
		return super.setText(text);
	}

	@Override
	public TextFinder setNegative(boolean negative) {
		throw new UnsupportedOperationException("Negative is invalid for Pattern!");
	}

	@Override
	public int start(int from) {
		if (matcher.find(from)) {
			final int end = matcher.end();
			// 只有匹配到的字符串结尾在limit范围内，才算找到
			if(end <= getValidEndIndex()){
				final int start = matcher.start();
				if(start == end){
					// issue#3421，如果匹配空串，按照未匹配对待，避免死循环
					return INDEX_NOT_FOUND;
				}

				return start;
			}
		}
		return INDEX_NOT_FOUND;
	}

	@Override
	public int end(int start) {
		final int end = matcher.end();
		final int limit;
		if(endIndex < 0){
			limit = text.length();
		}else{
			limit = Math.min(endIndex, text.length());
		}
		return end <= limit ? end : INDEX_NOT_FOUND;
	}

	@Override
	public PatternFinder reset() {
		this.matcher.reset();
		return this;
	}
}
