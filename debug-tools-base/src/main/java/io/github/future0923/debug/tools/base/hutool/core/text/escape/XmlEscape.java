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
