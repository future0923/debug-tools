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
package io.github.future0923.debug.tools.base.hutool.core.io.file;

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.LineHandler;
import io.github.future0923.debug.tools.base.hutool.core.io.watch.SimpleWatcher;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 行处理的Watcher实现
 *
 * @author looly
 * @since 4.5.2
 */
public class LineReadWatcher extends SimpleWatcher implements Runnable {

	private final RandomAccessFile randomAccessFile;
	private final Charset charset;
	private final LineHandler lineHandler;

	/**
	 * 构造
	 *
	 * @param randomAccessFile {@link RandomAccessFile}
	 * @param charset 编码
	 * @param lineHandler 行处理器{@link LineHandler}实现
	 */
	public LineReadWatcher(RandomAccessFile randomAccessFile, Charset charset, LineHandler lineHandler) {
		this.randomAccessFile = randomAccessFile;
		this.charset = charset;
		this.lineHandler = lineHandler;
	}

	@Override
	public void run() {
		onModify(null, null);
	}

	@Override
	public void onModify(WatchEvent<?> event, Path currentPath) {
		final RandomAccessFile randomAccessFile = this.randomAccessFile;
		final Charset charset = this.charset;
		final LineHandler lineHandler = this.lineHandler;

		try {
			final long currentLength = randomAccessFile.length();
			final long position = randomAccessFile.getFilePointer();
			if (position == currentLength) {
				// 内容长度不变时忽略此次事件
				return;
			} else if (currentLength < position) {
				// 如果内容变短或变0，说明文件做了删改或清空，回到内容末尾或0
				randomAccessFile.seek(currentLength);
				return;
			}

			// 读取行
			FileUtil.readLines(randomAccessFile, charset, lineHandler);

			// 记录当前读到的位置
			randomAccessFile.seek(currentLength);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
	}
}
