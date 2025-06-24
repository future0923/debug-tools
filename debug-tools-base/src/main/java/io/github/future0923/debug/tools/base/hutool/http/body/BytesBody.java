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
package io.github.future0923.debug.tools.base.hutool.http.body;

import io.github.future0923.debug.tools.base.hutool.core.io.IoUtil;

import java.io.OutputStream;

/**
 * bytes类型的Http request body，主要发送编码后的表单数据或rest body（如JSON或XML）
 *
 * @since 5.7.17
 * @author looly
 */
public class BytesBody implements RequestBody {

	private final byte[] content;

	/**
	 * 创建 Http request body
	 * @param content body内容，编码后
	 * @return BytesBody
	 */
	public static BytesBody create(byte[] content){
		return new BytesBody(content);
	}

	/**
	 * 构造
	 *
	 * @param content Body内容，编码后
	 */
	public BytesBody(byte[] content) {
		this.content = content;
	}

	@Override
	public void write(OutputStream out) {
		IoUtil.write(out, false, content);
	}
}
