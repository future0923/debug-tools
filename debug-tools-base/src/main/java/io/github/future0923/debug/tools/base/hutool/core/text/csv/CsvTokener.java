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

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

/**
 * CSV解析器，用于解析CSV文件
 *
 * @author looly
 * @since 5.8.0
 */
public class CsvTokener implements Closeable {

	private final Reader raw;
	/**
	 * 在Reader的位置（解析到第几个字符）
	 */
	private long index;
	/**
	 * 前一个字符
	 */
	private int prev;
	/**
	 * 是否使用前一个字符
	 */
	private boolean usePrev;

	/**
	 * 构造
	 *
	 * @param reader {@link Reader}
	 */
	public CsvTokener(final Reader reader) {
		this.raw = IoUtil.toBuffered(reader);
	}

	/**
	 * 读取下一个字符，并记录位置
	 *
	 * @return 下一个字符
	 */
	public int next() {
		if(this.usePrev){
			this.usePrev = false;
		}else{
			try {
				this.prev = this.raw.read();
			} catch (final IOException e) {
				throw new IORuntimeException(e);
			}
		}
		this.index++;
		return this.prev;
	}

	/**
	 * 将标记回退到第一个字符
	 *
	 * @throws IllegalStateException 当多次调用back时，抛出此异常
	 */
	public void back() throws IllegalStateException {
		if (this.usePrev || this.index <= 0) {
			throw new IllegalStateException("Stepping back two steps is not supported");
		}
		this.index --;
		this.usePrev = true;
	}

	/**
	 * 获取当前位置
	 *
	 * @return 位置
	 */
	public long getIndex() {
		return this.index;
	}

	@Override
	public void close() throws IOException {
		IoUtil.close(this.raw);
	}
}
