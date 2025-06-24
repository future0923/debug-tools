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
package io.github.future0923.debug.tools.base.hutool.core.io.resource;

import io.github.future0923.debug.tools.base.hutool.core.io.IORuntimeException;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * 基于byte[]的资源获取器<br>
 * 注意：此对象中getUrl方法始终返回null
 *
 * @author looly
 * @since 4.0.9
 */
public class BytesResource implements Resource, Serializable {
	private static final long serialVersionUID = 1L;

	private final byte[] bytes;
	private final String name;

	/**
	 * 构造
	 *
	 * @param bytes 字节数组
	 */
	public BytesResource(byte[] bytes) {
		this(bytes, null);
	}

	/**
	 * 构造
	 *
	 * @param bytes 字节数组
	 * @param name 资源名称
	 */
	public BytesResource(byte[] bytes, String name) {
		this.bytes = bytes;
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public InputStream getStream() {
		return new ByteArrayInputStream(this.bytes);
	}

	@Override
	public String readStr(Charset charset) throws IORuntimeException {
		return StrUtil.str(this.bytes, charset);
	}

	@Override
	public byte[] readBytes() throws IORuntimeException {
		return this.bytes;
	}

}
