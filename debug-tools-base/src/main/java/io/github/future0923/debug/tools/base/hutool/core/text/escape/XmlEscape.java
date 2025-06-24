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

import io.github.future0923.debug.tools.base.hutool.core.text.replacer.LookupReplacer;
import io.github.future0923.debug.tools.base.hutool.core.text.replacer.ReplacerChain;

/**
 * XML特殊字符转义<br>
 * 见：https://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents<br>
 *
 * <pre>
 * 	 &amp; (ampersand) 替换为 &amp;amp;
 * 	 &lt; (less than) 替换为 &amp;lt;
 * 	 &gt; (greater than) 替换为 &amp;gt;
 * 	 &quot; (double quote) 替换为 &amp;quot;
 * 	 ' (single quote / apostrophe) 替换为 &amp;apos;
 * </pre>
 *
 * @author looly
 * @since 5.7.2
 */
public class XmlEscape extends ReplacerChain {
	private static final long serialVersionUID = 1L;

	protected static final String[][] BASIC_ESCAPE = { //
			{"'", "&apos;"}, // " - single-quote
			{"\"", "&quot;"}, // " - double-quote
			{"&", "&amp;"}, // & - ampersand
			{"<", "&lt;"}, // < - less-than
			{">", "&gt;"}, // > - greater-than
	};

	/**
	 * 构造
	 */
	public XmlEscape() {
		addChain(new LookupReplacer(BASIC_ESCAPE));
	}
}
