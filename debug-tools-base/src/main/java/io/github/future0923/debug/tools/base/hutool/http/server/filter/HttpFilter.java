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
package io.github.future0923.debug.tools.base.hutool.http.server.filter;

import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerRequest;
import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerResponse;
import com.sun.net.httpserver.Filter;

import java.io.IOException;

/**
 * 过滤器接口，用于简化{@link Filter} 使用
 *
 * @author looly
 * @since 5.5.7
 */
@FunctionalInterface
public interface HttpFilter {

	/**
	 * 执行过滤
	 * @param req {@link HttpServerRequest} 请求对象，用于获取请求内容
	 * @param res {@link HttpServerResponse} 响应对象，用于写出内容
	 * @param chain {@link Filter.Chain}
	 * @throws IOException IO异常
	 */
	void doFilter(HttpServerRequest req, HttpServerResponse res, Filter.Chain chain) throws IOException;
}
