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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 限制读取最大长度的{@link FilterInputStream} 实现<br>
 * 来自：https://github.com/skylot/jadx/blob/master/jadx-plugins/jadx-plugins-api/src/main/java/jadx/api/plugins/utils/LimitedInputStream.java
 *
 * @author jadx
 */
public class LimitedInputStream extends FilterInputStream {

	private final long maxSize;
	private long currentPos;

	/**
	 * 构造
	 * @param in {@link InputStream}
	 * @param maxSize 限制最大读取量，单位byte
	 */
	public LimitedInputStream(InputStream in, long maxSize) {
		super(in);
		this.maxSize = maxSize;
	}

	@Override
	public int read() throws IOException {
		final int data = super.read();
		if (data != -1) {
			currentPos++;
			checkPos();
		}
		return data;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		final int count = super.read(b, off, len);
		if (count > 0) {
			currentPos += count;
			checkPos();
		}
		return count;
	}

	@Override
	public long skip(long n) throws IOException {
		final long skipped = super.skip(n);
		if (skipped != 0) {
			currentPos += skipped;
			checkPos();
		}
		return skipped;
	}

	private void checkPos() {
		if (currentPos > maxSize) {
			throw new IllegalStateException("Read limit exceeded");
		}
	}
}
