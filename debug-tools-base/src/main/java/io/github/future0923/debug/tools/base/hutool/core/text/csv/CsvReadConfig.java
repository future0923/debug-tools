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
package io.github.future0923.debug.tools.base.hutool.core.text.csv;


import java.io.Serializable;

/**
 * CSV读取配置项
 *
 * @author looly
 *
 */
public class CsvReadConfig extends CsvConfig<CsvReadConfig> implements Serializable {
	private static final long serialVersionUID = 5396453565371560052L;

	/** 指定标题行号，-1表示无标题行 */
	protected long headerLineNo = -1;
	/** 是否跳过空白行，默认true */
	protected boolean skipEmptyRows = true;
	/** 每行字段个数不同时是否抛出异常，默认false */
	protected boolean errorOnDifferentFieldCount;
	/** 定义开始的行（包括），此处为原始文件行号 */
	protected long beginLineNo;
	/** 结束的行（包括），此处为原始文件行号 */
	protected long endLineNo = Long.MAX_VALUE-1;
	/** 每个字段是否去除两边空白符 */
	protected boolean trimField;

	/**
	 * 默认配置
	 *
	 * @return 默认配置
	 */
	public static CsvReadConfig defaultConfig() {
		return new CsvReadConfig();
	}

	/**
	 * 设置是否首行做为标题行，默认false<br>
	 * 当设置为{@code true}时，默认标题行号是{@link #beginLineNo}，{@code false}为-1，表示无行号
	 *
	 * @param containsHeader 是否首行做为标题行，默认false
	 * @return this
	 * @see #setHeaderLineNo(long)
	 */
	public CsvReadConfig setContainsHeader(boolean containsHeader) {
		return setHeaderLineNo(containsHeader ? beginLineNo : -1);
	}

	/**
	 * 设置标题行行号，默认-1，表示无标题行<br>
	 *
	 * @param headerLineNo 标题行行号，-1表示无标题行
	 * @return this
	 * @since 5.7.23
	 */
	public CsvReadConfig setHeaderLineNo(long headerLineNo) {
		this.headerLineNo = headerLineNo;
		return this;
	}

	/**
	 * 设置是否跳过空白行，默认true
	 *
	 * @param skipEmptyRows 是否跳过空白行，默认true
	 * @return this
	 */
	public CsvReadConfig setSkipEmptyRows(boolean skipEmptyRows) {
		this.skipEmptyRows = skipEmptyRows;
		return this;
	}

	/**
	 * 设置每行字段个数不同时是否抛出异常，默认false
	 *
	 * @param errorOnDifferentFieldCount 每行字段个数不同时是否抛出异常，默认false
	 * @return this
	 */
	public CsvReadConfig setErrorOnDifferentFieldCount(boolean errorOnDifferentFieldCount) {
		this.errorOnDifferentFieldCount = errorOnDifferentFieldCount;
		return this;
	}

	/**
	 * 设置开始的行（包括），默认0，此处为原始文件行号
	 *
	 * @param beginLineNo 开始的行号（包括）
	 * @return this
	 * @since 5.7.4
	 */
	public CsvReadConfig setBeginLineNo(long beginLineNo) {
		this.beginLineNo = beginLineNo;
		return this;
	}

	/**
	 * 设置结束的行（包括），默认不限制，此处为原始文件行号
	 *
	 * @param endLineNo 结束的行号（包括）
	 * @return this
	 * @since 5.7.4
	 */
	public CsvReadConfig setEndLineNo(long endLineNo) {
		this.endLineNo = endLineNo;
		return this;
	}

	/**
	 * 设置每个字段是否去除两边空白符<br>
	 * 如果字段以{@link #textDelimiter}包围，则保留两边空格
	 *
	 * @param trimField 去除两边空白符
	 * @return this
	 * @since 5.7.13
	 */
	public CsvReadConfig setTrimField(boolean trimField) {
		this.trimField = trimField;
		return this;
	}
}
