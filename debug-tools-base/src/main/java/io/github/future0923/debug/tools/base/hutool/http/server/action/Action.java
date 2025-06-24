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
package io.github.future0923.debug.tools.base.hutool.http.server.action;

import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerRequest;
import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerResponse;

import java.io.IOException;

/**
 * 请求处理接口<br>
 * 当用户请求某个Path，则调用相应Action的doAction方法
 *
 * @author Looly
 * @since 5.2.6
 */
@FunctionalInterface
public interface Action {

	/**
	 * 处理请求
	 *
	 * @param request  请求对象
	 * @param response 响应对象
	 * @throws IOException IO异常
	 */
	void doAction(HttpServerRequest request, HttpServerResponse response) throws IOException;
}
