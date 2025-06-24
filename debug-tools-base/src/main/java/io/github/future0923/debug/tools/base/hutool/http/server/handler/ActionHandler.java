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
package io.github.future0923.debug.tools.base.hutool.http.server.handler;

import io.github.future0923.debug.tools.base.hutool.http.server.HttpExchangeWrapper;
import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerRequest;
import io.github.future0923.debug.tools.base.hutool.http.server.HttpServerResponse;
import io.github.future0923.debug.tools.base.hutool.http.server.action.Action;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Action处理器，用于将HttpHandler转换为Action形式
 *
 * @author looly
 * @since 5.2.6
 */
public class ActionHandler implements HttpHandler {

	private final Action action;

	/**
	 * 构造
	 *
	 * @param action Action
	 */
	public ActionHandler(Action action) {
		this.action = action;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		final HttpServerRequest request;
		final HttpServerResponse response;
		if (httpExchange instanceof HttpExchangeWrapper) {
			// issue#3343 当使用Filter时，可能读取了请求参数，此时使用共享的req和res，可复用缓存
			final HttpExchangeWrapper wrapper = (HttpExchangeWrapper) httpExchange;
			request = wrapper.getRequest();
			response = wrapper.getResponse();
		} else {
			request = new HttpServerRequest(httpExchange);
			response = new HttpServerResponse(httpExchange);
		}
		action.doAction(request, response);
		httpExchange.close();
	}
}
