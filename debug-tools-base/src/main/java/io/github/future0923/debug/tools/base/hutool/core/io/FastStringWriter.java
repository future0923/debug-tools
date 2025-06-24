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
package io.github.future0923.debug.tools.base.hutool.core.io;

import io.github.future0923.debug.tools.base.hutool.core.text.StrBuilder;

import java.io.Writer;

/**
 * 借助{@link StrBuilder} 提供快读的字符串写出，相比jdk的StringWriter非线程安全，速度更快。
 *
 * @author looly
 * @since 5.3.3
 */
public final class FastStringWriter extends Writer {

	private final StrBuilder builder;

	/**
	 * 构造
	 */
	public FastStringWriter() {
		this(StrBuilder.DEFAULT_CAPACITY);
	}

	/**
	 * 构造
	 *
	 * @param initialSize 初始容量
	 */
	public FastStringWriter(int initialSize) {
		if (initialSize < 0) {
			initialSize = StrBuilder.DEFAULT_CAPACITY;
		}
		this.builder = new StrBuilder(initialSize);
	}


	@Override
	public void write(final int c) {
		this.builder.append((char) c);
	}


	@Override
	public void write(final String str) {
		this.builder.append(str);
	}


	@Override
	public void write(final String str, final int off, final int len) {
		this.builder.append(str, off, off + len);
	}


	@Override
	public void write(final char[] cbuf) {
		this.builder.append(cbuf, 0, cbuf.length);
	}


	@Override
	public void write(final char[] cbuf, final int off, final int len) {
		if ((off < 0) || (off > cbuf.length) || (len < 0) ||
				((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		this.builder.append(cbuf, off, len);
	}


	@Override
	public void flush() {
		// Nothing to be flushed
	}


	@Override
	public void close() {
		// Nothing to be closed
	}


	@Override
	public String toString() {
		return this.builder.toString();
	}

}
