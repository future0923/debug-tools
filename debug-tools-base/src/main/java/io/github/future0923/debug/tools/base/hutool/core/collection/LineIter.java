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
package io.github.future0923.debug.tools.base.hutool.core.collection;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;
import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * 将Reader包装为一个按照行读取的Iterator<br>
 * 此对象遍历结束后，应关闭之，推荐使用方式:
 *
 * <pre>
 * LineIterator it = null;
 * try {
 * 	it = new LineIterator(reader);
 * 	while (it.hasNext()) {
 * 		String line = it.nextLine();
 * 		// do something with line
 * 	}
 * } finally {
 * 		it.close();
 * }
 * </pre>
 *
 * 此类来自于Apache Commons io
 *
 * @author looly
 * @since 4.1.1
 */
public class LineIter extends ComputeIter<String> implements IterableIter<String>, Closeable, Serializable {
	private static final long serialVersionUID = 1L;

	private final BufferedReader bufferedReader;

	/**
	 * 构造
	 *
	 * @param in {@link InputStream}
	 * @param charset 编码
	 * @throws IllegalArgumentException reader为null抛出此异常
	 */
	public LineIter(InputStream in, Charset charset) throws IllegalArgumentException {
		this(IoUtil.getReader(in, charset));
	}

	/**
	 * 构造
	 *
	 * @param reader {@link Reader}对象，不能为null
	 * @throws IllegalArgumentException reader为null抛出此异常
	 */
	public LineIter(Reader reader) throws IllegalArgumentException {
		Assert.notNull(reader, "Reader must not be null");
		this.bufferedReader = IoUtil.getReader(reader);
	}

	// -----------------------------------------------------------------------
	@Override
	protected String computeNext() {
		try {
			while (true) {
				String line = bufferedReader.readLine();
				if (line == null) {
					return null;
				} else if (isValidLine(line)) {
					return line;
				}
				// 无效行，则跳过进入下一行
			}
		} catch (IOException ioe) {
			close();
			throw new IORuntimeException(ioe);
		}
	}

	/**
	 * 关闭Reader
	 */
	@Override
	public void close() {
		super.finish();
		IoUtil.close(bufferedReader);
	}

	/**
	 * 重写此方法来判断是否每一行都被返回，默认全部为true
	 *
	 * @param line 需要验证的行
	 * @return 是否通过验证
	 */
	protected boolean isValidLine(String line) {
		return true;
	}
}
