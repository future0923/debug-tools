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
import io.github.future0923.debug.tools.base.constants.ProjectConstants;

/**
 * @author future0923
 */
public class IndexHttpHandler extends BaseHttpHandler<String, String> {

    public static final IndexHttpHandler INSTANCE = new IndexHttpHandler();

    public static final String PATH = "/";

    private IndexHttpHandler() {

    }

    @Override
    protected String doHandle(String req, Headers responseHeaders) {
        return "Hello " + ProjectConstants.NAME + " " + ProjectConstants.VERSION;
    }
}
