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
package io.github.future0923.debug.tools.server.http.handler;

import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.github.future0923.debug.tools.base.utils.DebugToolsIOUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
public abstract class BaseHttpHandler<Req, Res> implements HttpHandler {

    private final Class<Req> reqClass;

    @SuppressWarnings("unchecked")
    public BaseHttpHandler() {
        this.reqClass = (Class<Req>) ClassUtil.getTypeArgument(getClass());
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        String requestBody = new String(DebugToolsIOUtils.readAllBytes(inputStream), StandardCharsets.UTF_8);
        Req req;
        if (reqClass.isAssignableFrom(Void.class)) {
            req = null;
        } else {
            req = DebugToolsJsonUtils.toBean(requestBody, reqClass);
        }
        Headers responseHeaders = httpExchange.getResponseHeaders();
        Res res = doHandle(req, responseHeaders);
        String responseBody = "";
        if (res != null) {
            if (res instanceof String) {
                responseBody = (String) res;
            } else {
                responseBody = DebugToolsJsonUtils.toJsonPrettyStr(res);
            }
        }
        responseHeaders.set("Content-Type", "application/json; charset=UTF-8");
        responseHeaders.set("Access-Control-Allow-Origin", "*");
        responseHeaders.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(200, bytes.length);
        // 返回响应
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(bytes);
        outputStream.close();
    }

    protected abstract Res doHandle(Req req, Headers responseHeaders);
}
