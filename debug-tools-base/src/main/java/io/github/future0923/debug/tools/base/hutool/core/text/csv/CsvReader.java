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

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * CSV文件读取器，参考：FastCSV
 *
 * @author Looly
 * @since 4.0.1
 */
public class CsvReader extends CsvBaseReader implements Iterable<CsvRow>, Closeable {
	private static final long serialVersionUID = 1L;

	private final Reader reader;

	//--------------------------------------------------------------------------------------------- Constructor start

	/**
	 * 构造，使用默认配置项
	 */
	public CsvReader() {
		this(null);
	}

	/**
	 * 构造
	 *
	 * @param config 配置项
	 */
	public CsvReader(CsvReadConfig config) {
		this((Reader) null, config);
	}

	/**
	 * 构造，默认{@link #DEFAULT_CHARSET}编码
	 *
	 * @param file   CSV文件路径，null表示不设置路径
	 * @param config 配置项，null表示默认配置
	 * @since 5.0.4
	 */
	public CsvReader(File file, CsvReadConfig config) {
		this(file, DEFAULT_CHARSET, config);
	}

	/**
	 * 构造，默认{@link #DEFAULT_CHARSET}编码
	 *
	 * @param path   CSV文件路径，null表示不设置路径
	 * @param config 配置项，null表示默认配置
	 * @since 5.0.4
	 */
	public CsvReader(Path path, CsvReadConfig config) {
		this(path, DEFAULT_CHARSET, config);
	}

	/**
	 * 构造
	 *
	 * @param file    CSV文件路径，null表示不设置路径
	 * @param charset 编码
	 * @param config  配置项，null表示默认配置
	 * @since 5.0.4
	 */
	public CsvReader(File file, Charset charset, CsvReadConfig config) {
		this(FileUtil.getReader(file, charset), config);
	}

	/**
	 * 构造
	 *
	 * @param path    CSV文件路径，null表示不设置路径
	 * @param charset 编码
	 * @param config  配置项，null表示默认配置
	 * @since 5.0.4
	 */
	public CsvReader(Path path, Charset charset, CsvReadConfig config) {
		this(FileUtil.getReader(path, charset), config);
	}

	/**
	 * 构造
	 *
	 * @param reader {@link Reader}，null表示不设置默认reader
	 * @param config 配置项，null表示默认配置
	 * @since 5.0.4
	 */
	public CsvReader(Reader reader, CsvReadConfig config) {
		super(config);
		this.reader = reader;
	}
	//--------------------------------------------------------------------------------------------- Constructor end
	/**
	 * 读取CSV文件，此方法只能调用一次<br>
	 * 调用此方法的前提是构造中传入文件路径或Reader
	 *
	 * @return {@link CsvData}，包含数据列表和行信息
	 * @throws IORuntimeException IO异常
	 */
	public CsvData read() throws IORuntimeException {
		return read(this.reader, false);
	}

	/**
	 * 读取CSV数据，此方法只能调用一次<br>
	 * 调用此方法的前提是构造中传入文件路径或Reader
	 *
	 * @param rowHandler 行处理器，用于一行一行的处理数据
	 * @throws IORuntimeException IO异常
	 * @since 5.0.4
	 */
	public void read(CsvRowHandler rowHandler) throws IORuntimeException {
		read(this.reader, false, rowHandler);
	}

	/**
	 * 根据Reader创建{@link Stream}，以便使用stream方式读取csv行
	 *
	 * @return {@link Stream}
	 * @since 5.7.14
	 */
	public Stream<CsvRow> stream() {
		return StreamSupport.stream(spliterator(), false)
				.onClose(() -> {
					try {
						close();
					} catch (final IOException e) {
						throw new IORuntimeException(e);
					}
				});
	}

	@Override
	public Iterator<CsvRow> iterator() {
		return parse(this.reader);
	}

	@Override
	public void close() throws IOException {
		IoUtil.close(this.reader);
	}
}
