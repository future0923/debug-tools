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
