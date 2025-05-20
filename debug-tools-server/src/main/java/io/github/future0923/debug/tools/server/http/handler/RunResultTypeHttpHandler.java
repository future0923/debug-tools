/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                return DebugToolsJsonUtils.toJsonPrettyStr(valueByOffset);
            } catch (Exception e) {
                return "{\n\"result\": \"" + e.getMessage()+"\"\n}";
            }
        } else if (PrintResultType.DEBUG.getType().equals(req.getPrintResultType())) {
            return new RunResultDTO("result", valueByOffset, RunResultDTO.Type.ROOT, offsetPath);
        }
        return null;
    }
}
