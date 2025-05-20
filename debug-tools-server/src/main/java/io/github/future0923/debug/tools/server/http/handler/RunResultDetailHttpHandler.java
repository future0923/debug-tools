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
import io.github.future0923.debug.tools.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;

import java.util.List;

/**
 * @author future0923
 */
public class RunResultDetailHttpHandler extends BaseHttpHandler<RunResultDetailReq, List<RunResultDTO>> {

    public static final RunResultDetailHttpHandler INSTANCE = new RunResultDetailHttpHandler();

    public static final String PATH = "/result/detail";

    private RunResultDetailHttpHandler() {

    }
    @Override
    protected List<RunResultDTO> doHandle(RunResultDetailReq req, Headers responseHeaders) {
        String offsetPath = req.getOffsetPath();
        Object valueByOffset = DebugToolsResultUtils.getValueByOffset(offsetPath);
        return DebugToolsResultUtils.convertRunResultDTO(valueByOffset, offsetPath);
    }
}
