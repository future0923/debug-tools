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

import com.sun.net.httpserver.Headers;
import java.util.UUID;

/**
 * 暴露调试协议能力和当前 JVM 的随机标识；IDEA 通过 JDI 读取同一字段来匹配会话。
 */
public final class GroovyDebugHttpHandler extends BaseHttpHandler<Void, String> {

    public static final String PATH = "/groovy/debug/runtime";
    public static final String RUNTIME_ID = UUID.randomUUID().toString();
    public static final GroovyDebugHttpHandler INSTANCE = new GroovyDebugHttpHandler();

    private GroovyDebugHttpHandler() {
    }

    @Override
    protected String doHandle(Void req, Headers responseHeaders) {
        return RUNTIME_ID;
    }
}
