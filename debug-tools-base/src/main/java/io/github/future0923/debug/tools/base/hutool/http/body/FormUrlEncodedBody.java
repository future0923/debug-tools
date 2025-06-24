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

import io.github.future0923.debug.tools.base.hutool.core.net.url.UrlQuery;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * application/x-www-form-urlencoded 类型请求body封装
 *
 * @author looly
 * @since 5.7.17
 */
public class FormUrlEncodedBody extends BytesBody {

	/**
	 * 创建 Http request body
	 *
	 * @param form    表单
	 * @param charset 编码
	 * @return FormUrlEncodedBody
	 */
	public static FormUrlEncodedBody create(Map<String, Object> form, Charset charset) {
		return new FormUrlEncodedBody(form, charset);
	}

	/**
	 * 构造
	 *
	 * @param form    表单
	 * @param charset 编码
	 */
	public FormUrlEncodedBody(Map<String, Object> form, Charset charset) {
		super(StrUtil.bytes(UrlQuery.of(form, true).build(charset), charset));
	}

	@Override
	public String toString() {
		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		write(result);
		return result.toString();
	}
}
