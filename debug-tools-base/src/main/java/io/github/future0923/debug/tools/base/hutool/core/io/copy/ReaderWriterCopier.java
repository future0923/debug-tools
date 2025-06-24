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
package io.github.future0923.debug.tools.base.hutool.core.io.copy;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.StreamProgress;
import io.github.future0923.debug.tools.base.hutool.core.lang.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * {@link Reader} 向 {@link Writer} 拷贝
 *
 * @author looly
 * @since 5.7.8
 */
public class ReaderWriterCopier extends IoCopier<Reader, Writer> {

	/**
	 * 构造
	 */
	public ReaderWriterCopier() {
		this(IoUtil.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 构造
	 *
	 * @param bufferSize 缓存大小
	 */
	public ReaderWriterCopier(int bufferSize) {
		this(bufferSize, -1);
	}

	/**
	 * 构造
	 *
	 * @param bufferSize 缓存大小
	 * @param count      拷贝总数
	 */
	public ReaderWriterCopier(int bufferSize, long count) {
		this(bufferSize, count, null);
	}

	/**
	 * 构造
	 *
	 * @param bufferSize 缓存大小
	 * @param count      拷贝总数
	 * @param progress   进度条
	 */
	public ReaderWriterCopier(int bufferSize, long count, StreamProgress progress) {
		super(bufferSize, count, progress);
	}

	@Override
	public long copy(Reader source, Writer target) {
		Assert.notNull(source, "InputStream is null !");
		Assert.notNull(target, "OutputStream is null !");

		final StreamProgress progress = this.progress;
		if (null != progress) {
			progress.start();
		}
		final long size;
		try {
			size = doCopy(source, target, new char[bufferSize(this.count)], progress);
			target.flush();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}

		if (null != progress) {
			progress.finish();
		}
		return size;
	}

	/**
	 * 执行拷贝，如果限制最大长度，则按照最大长度读取，否则一直读取直到遇到-1
	 *
	 * @param source   {@link InputStream}
	 * @param target   {@link OutputStream}
	 * @param buffer   缓存
	 * @param progress 进度条
	 * @return 拷贝总长度
	 * @throws IOException IO异常
	 */
	private long doCopy(Reader source, Writer target, char[] buffer, StreamProgress progress) throws IOException {
		long numToRead = this.count > 0 ? this.count : Long.MAX_VALUE;
		long total = 0;

		int read;
		while (numToRead > 0) {
			read = source.read(buffer, 0, bufferSize(numToRead));
			if (read < 0) {
				// 提前读取到末尾
				break;
			}
			target.write(buffer, 0, read);
			if(flushEveryBuffer){
				target.flush();
			}

			numToRead -= read;
			total += read;
			if (null != progress) {
				progress.progress(this.count, total);
			}
		}

		return total;
	}
}
