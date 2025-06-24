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
package io.github.future0923.debug.tools.base.hutool.core.text.csv;

import io.github.future0923.debug.tools.base.hutool.core.util.CharUtil;

import java.io.Serializable;

/**
 * CSV写出配置项
 *
 * @author looly
 */
public class CsvWriteConfig extends CsvConfig<CsvWriteConfig> implements Serializable {
	private static final long serialVersionUID = 5396453565371560052L;

	/**
	 * 是否始终使用文本分隔符，文本包装符，默认false，按需添加
	 */
	protected boolean alwaysDelimitText;
	/**
	 * 换行符
	 */
	protected char[] lineDelimiter = {CharUtil.CR, CharUtil.LF};

	/**
	 * 文件末尾是否添加换行符<br>
	 * 按照https://datatracker.ietf.org/doc/html/rfc4180#section-2 规范，末尾换行符可有可无。
	 */
	protected boolean endingLineBreak;

	/**
	 * 默认配置
	 *
	 * @return 默认配置
	 */
	public static CsvWriteConfig defaultConfig() {
		return new CsvWriteConfig();
	}

	/**
	 * 设置是否始终使用文本分隔符，文本包装符，默认false，按需添加
	 *
	 * @param alwaysDelimitText 是否始终使用文本分隔符，文本包装符，默认false，按需添加
	 * @return this
	 */
	public CsvWriteConfig setAlwaysDelimitText(boolean alwaysDelimitText) {
		this.alwaysDelimitText = alwaysDelimitText;
		return this;
	}

	/**
	 * 设置换行符
	 *
	 * @param lineDelimiter 换行符
	 * @return this
	 */
	public CsvWriteConfig setLineDelimiter(char[] lineDelimiter) {
		this.lineDelimiter = lineDelimiter;
		return this;
	}

	/**
	 * 文件末尾是否添加换行符<br>
	 * 按照https://datatracker.ietf.org/doc/html/rfc4180#section-2 规范，末尾换行符可有可无。
	 *
	 * @param endingLineBreak 文件末尾是否添加换行符
	 * @return this
	 */
	public CsvWriteConfig setEndingLineBreak(boolean endingLineBreak) {
		this.endingLineBreak = endingLineBreak;
		return this;
	}
}
