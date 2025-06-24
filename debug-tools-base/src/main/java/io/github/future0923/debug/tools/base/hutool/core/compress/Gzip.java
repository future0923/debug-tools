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
package io.github.future0923.debug.tools.base.hutool.core.compress;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP是用于Unix系统的文件压缩<br>
 * gzip的基础是DEFLATE
 *
 * @author looly
 * @since 5.7.8
 */
public class Gzip implements Closeable {

	private InputStream source;
	private OutputStream target;

	/**
	 * 创建Gzip
	 *
	 * @param source 源流
	 * @param target 目标流
	 * @return Gzip
	 */
	public static Gzip of(InputStream source, OutputStream target) {
		return new Gzip(source, target);
	}

	/**
	 * 构造
	 *
	 * @param source 源流
	 * @param target 目标流
	 */
	public Gzip(InputStream source, OutputStream target) {
		this.source = source;
		this.target = target;
	}

	/**
	 * 获取目标流
	 *
	 * @return 目标流
	 */
	public OutputStream getTarget() {
		return this.target;
	}

	/**
	 * 将普通数据流压缩
	 *
	 * @return Gzip
	 */
	public Gzip gzip() {
		try {
			target = (target instanceof GZIPOutputStream) ?
					(GZIPOutputStream) target : new GZIPOutputStream(target);
			IoUtil.copy(source, target);
			((GZIPOutputStream) target).finish();
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	/**
	 * 将压缩流解压到target中
	 *
	 * @return Gzip
	 */
	public Gzip unGzip() {
		try {
			source = (source instanceof GZIPInputStream) ?
					(GZIPInputStream) source : new GZIPInputStream(source);
			IoUtil.copy(source, target);
		} catch (IOException e) {
			throw new IORuntimeException(e);
		}
		return this;
	}

	@Override
	public void close() {
		IoUtil.close(this.target);
		IoUtil.close(this.source);
	}
}
