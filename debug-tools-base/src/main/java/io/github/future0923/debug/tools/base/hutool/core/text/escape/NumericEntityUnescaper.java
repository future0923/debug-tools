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

import io.github.future0923.debug.tools.base.hutool.core.text.StrBuilder;
import io.github.future0923.debug.tools.base.hutool.core.text.replacer.StrReplacer;
import io.github.future0923.debug.tools.base.hutool.core.util.CharUtil;

/**
 * 形如&#39;的反转义器
 *
 * @author looly
 *
 */
public class NumericEntityUnescaper extends StrReplacer {
	private static final long serialVersionUID = 1L;

	@Override
	protected int replace(CharSequence str, int pos, StrBuilder out) {
		final int len = str.length();
		// 检查以确保以&#开头
		if (str.charAt(pos) == '&' && pos < len - 2 && str.charAt(pos + 1) == '#') {
			int start = pos + 2;
			boolean isHex = false;
			final char firstChar = str.charAt(start);
			if (firstChar == 'x' || firstChar == 'X') {
				start++;
				isHex = true;
			}

			// 确保&#后还有数字
			if (start == len) {
				return 0;
			}

			int end = start;
			while (end < len && CharUtil.isHexChar(str.charAt(end))) {
				end++;
			}
			final boolean isSemiNext = (end != len) && (str.charAt(end) == ';');
			if (isSemiNext) {
				int entityValue;
				try {
					if (isHex) {
						entityValue = Integer.parseInt(str.subSequence(start, end).toString(), 16);
					} else {
						entityValue = Integer.parseInt(str.subSequence(start, end).toString(), 10);
					}
				} catch (final NumberFormatException nfe) {
					return 0;
				}
				out.append((char)entityValue);
				return 2 + end - start + (isHex ? 1 : 0) + 1;
			}
		}
		return 0;
	}
}
