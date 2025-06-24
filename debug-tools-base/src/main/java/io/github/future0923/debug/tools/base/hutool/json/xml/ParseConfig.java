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
package io.github.future0923.debug.tools.base.hutool.json.xml;

import java.io.Serializable;

/**
 * XML解析为JSON的可选选项<br>
 * 参考：https://github.com/stleary/JSON-java/blob/master/src/main/java/org/json/ParserConfiguration.java
 *
 * @author AylwardJ, Looly
 */
public class ParseConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认最大嵌套深度
	 */
	public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = 512;

	/**
	 * 创建ParseConfig
	 *
	 * @return ParseConfig
	 */
	public static ParseConfig of() {
		return new ParseConfig();
	}

	/**
	 * 是否保持值为String类型，如果为{@code false}，则尝试转换为对应类型(numeric, boolean, string)
	 */
	private boolean keepStrings;
	/**
	 * 最大嵌套深度，用于解析时限制解析层级，当大于这个层级时抛出异常，-1表示无限制
	 */
	private int maxNestingDepth = -1;

	/**
	 * 是否保持值为String类型，如果为{@code false}，则尝试转换为对应类型(numeric, boolean, string)
	 *
	 * @return 是否保持值为String类型
	 */
	public boolean isKeepStrings() {
		return keepStrings;
	}

	/**
	 * 设置是否保持值为String类型，如果为{@code false}，则尝试转换为对应类型(numeric, boolean, string)
	 *
	 * @param keepStrings 是否保持值为String类型
	 * @return this
	 */
	public ParseConfig setKeepStrings(final boolean keepStrings) {
		this.keepStrings = keepStrings;
		return this;
	}

	/**
	 * 获取最大嵌套深度，用于解析时限制解析层级，当大于这个层级时抛出异常，-1表示无限制
	 *
	 * @return 最大嵌套深度
	 */
	public int getMaxNestingDepth() {
		return maxNestingDepth;
	}

	/**
	 * 设置最大嵌套深度，用于解析时限制解析层级，当大于这个层级时抛出异常，-1表示无限制
	 *
	 * @param maxNestingDepth 最大嵌套深度
	 * @return this
	 */
	public ParseConfig setMaxNestingDepth(final int maxNestingDepth) {
		this.maxNestingDepth = maxNestingDepth;
		return this;
	}
}
