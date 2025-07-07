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
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

/**
 * @author future0923
 */
public class RunResultTypeHttpHandler extends BaseHttpHandler<RunResultTypeReq, Object> {

    public static final RunResultTypeHttpHandler INSTANCE = new RunResultTypeHttpHandler();

    public static final String PATH = "/result/type";

    private RunResultTypeHttpHandler() {

    }

    @Override
    protected Object doHandle(RunResultTypeReq req, Headers responseHeaders) {
        String offsetPath = req.getOffsetPath();
        Object valueByOffset = DebugToolsResultUtils.getValueByOffset(offsetPath);
        if (valueByOffset == null) {
            return null;
        }
        if (PrintResultType.TOSTRING.getType().equals(req.getPrintResultType())) {
            return valueByOffset.toString();
        }
        if (PrintResultType.JSON.getType().equals(req.getPrintResultType())) {
            try {
                return DebugToolsJsonUtils.toJsonStr(valueByOffset);
            } catch (Exception e) {
                return "{\n\"result\": \"" + e.getMessage()+"\"\n}";
            }
        } else if (PrintResultType.DEBUG.getType().equals(req.getPrintResultType())) {
            return new RunResultDTO("result", valueByOffset, RunResultDTO.Type.ROOT, offsetPath);
        }
        return null;
    }
}
