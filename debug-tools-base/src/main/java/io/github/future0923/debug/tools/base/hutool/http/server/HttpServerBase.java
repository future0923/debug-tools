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
package io.github.future0923.debug.tools.base.hutool.http.server;

import io.github.future0923.debug.tools.base.hutool.core.util.CharsetUtil;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;

import java.io.Closeable;
import java.nio.charset.Charset;

/**
 * HttpServer公用对象，提供HttpExchange包装和公用方法
 *
 * @author looly
 * @since 5.2.6
 */
public class HttpServerBase implements Closeable {

	final static Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;

	final HttpExchange httpExchange;

	/**
	 * 构造
	 *
	 * @param httpExchange {@link HttpExchange}
	 */
	public HttpServerBase(HttpExchange httpExchange) {
		this.httpExchange = httpExchange;
	}

	/**
	 * 获取{@link HttpExchange}对象
	 *
	 * @return {@link HttpExchange}对象
	 */
	public HttpExchange getHttpExchange() {
		return this.httpExchange;
	}

	/**
	 * 获取{@link HttpContext}
	 *
	 * @return {@link HttpContext}
	 * @since 5.5.7
	 */
	public HttpContext getHttpContext() {
		return getHttpExchange().getHttpContext();
	}

	/**
	 * 调用{@link HttpExchange#close()}，关闭请求流和响应流
	 */
	@Override
	public void close() {
		this.httpExchange.close();
	}
}
