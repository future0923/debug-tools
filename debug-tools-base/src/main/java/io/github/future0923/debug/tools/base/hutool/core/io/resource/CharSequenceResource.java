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
import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.CharsetUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * {@link CharSequence}资源，字符串做为资源
 *
 * @author looly
 * @since 5.5.2
 */
public class CharSequenceResource implements Resource, Serializable {
	private static final long serialVersionUID = 1L;

	private final CharSequence data;
	private final CharSequence name;
	private final Charset charset;

	/**
	 * 构造，使用UTF8编码
	 *
	 * @param data 资源数据
	 */
	public CharSequenceResource(CharSequence data) {
		this(data, null);
	}

	/**
	 * 构造，使用UTF8编码
	 *
	 * @param data 资源数据
	 * @param name 资源名称
	 */
	public CharSequenceResource(CharSequence data, String name) {
		this(data, name, CharsetUtil.CHARSET_UTF_8);
	}

	/**
	 * 构造
	 *
	 * @param data 资源数据
	 * @param name 资源名称
	 * @param charset 编码
	 */
	public CharSequenceResource(CharSequence data, CharSequence name, Charset charset) {
		this.data = data;
		this.name = name;
		this.charset = charset;
	}

	@Override
	public String getName() {
		return StrUtil.str(this.name);
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public InputStream getStream() {
		return new ByteArrayInputStream(readBytes());
	}

	@Override
	public BufferedReader getReader(Charset charset) {
		return IoUtil.getReader(new StringReader(this.data.toString()));
	}

	@Override
	public String readStr(Charset charset) throws IORuntimeException {
		return this.data.toString();
	}

	@Override
	public byte[] readBytes() throws IORuntimeException {
		return this.data.toString().getBytes(this.charset);
	}

}
