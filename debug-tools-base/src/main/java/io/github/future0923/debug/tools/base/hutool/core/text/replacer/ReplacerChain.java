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
package io.github.future0923.debug.tools.base.hutool.core.text.replacer;

import io.github.future0923.debug.tools.base.hutool.core.lang.Chain;
import io.github.future0923.debug.tools.base.hutool.core.text.StrBuilder;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 字符串替换链，用于组合多个字符串替换逻辑
 *
 * @author looly
 * @since 4.1.5
 */
public class ReplacerChain extends StrReplacer implements Chain<StrReplacer, ReplacerChain> {
	private static final long serialVersionUID = 1L;

	private final List<StrReplacer> replacers = new LinkedList<>();

	/**
	 * 构造
	 *
	 * @param strReplacers 字符串替换器
	 */
	public ReplacerChain(StrReplacer... strReplacers) {
		for (StrReplacer strReplacer : strReplacers) {
			addChain(strReplacer);
		}
	}

	@SuppressWarnings("NullableProblems")
	@Override
	public Iterator<StrReplacer> iterator() {
		return replacers.iterator();
	}

	@Override
	public ReplacerChain addChain(StrReplacer element) {
		replacers.add(element);
		return this;
	}

	@Override
	protected int replace(CharSequence str, int pos, StrBuilder out) {
		int consumed = 0;
		for (StrReplacer strReplacer : replacers) {
			consumed = strReplacer.replace(str, pos, out);
			if (0 != consumed) {
				return consumed;
			}
		}
		return consumed;
	}

}
