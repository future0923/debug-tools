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
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.sql.SqlPrintInterceptor;

/**
 * @author future0923
 */
public class ChangePrintSqlTypeHttpHandler extends BaseHttpHandler<String, String> {

    private static final Logger logger = Logger.getLogger(ChangePrintSqlTypeHttpHandler.class);

    public static final ChangePrintSqlTypeHttpHandler INSTANCE = new ChangePrintSqlTypeHttpHandler();

    public static final String PATH = "/changePrintSqlType";

    private ChangePrintSqlTypeHttpHandler() {

    }

    @Override
    protected String doHandle(String printSqlType, Headers responseHeaders) {
        if (StrUtil.isBlank(printSqlType)) {
            return "";
        }
        SqlPrintInterceptor.setPrintSqlType(printSqlType);
        String type = SqlPrintInterceptor.printSqlType.getType();
        logger.info("change printSqlType to {}", type);
        return type;
    }
}
