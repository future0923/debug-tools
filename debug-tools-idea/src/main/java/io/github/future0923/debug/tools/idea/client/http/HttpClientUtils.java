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
package io.github.future0923.debug.tools.idea.client.http;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.base.hutool.http.HttpUtil;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.tools.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;

import java.io.IOException;
import java.util.List;

/**
 * @author future0923
 */
public class HttpClientUtils {

    private static final String RESULT_TYPE_URI = "/result/type";

    private static final String RESULT_DETAIL_URI = "/result/detail";

    private static final String ALL_CLASS_LOADER_URI = "/allClassLoader";

    private static final String GET_APPLICATION_NAME_URI = "/getApplicationName";

    private static final int TIMEOUT = 5000;

    public static String resultType(Project project, String offsetPath, String printResultType) {
        RunResultTypeReq req = new RunResultTypeReq();
        req.setPrintResultType(printResultType);
        req.setOffsetPath(offsetPath);
        try {
            return HttpUtil.post(DebugToolsSettingState.getInstance(project).getUrl(RESULT_TYPE_URI), DebugToolsJsonUtils.toJsonStr(req), TIMEOUT);
        } catch (Exception e) {
            if (PrintResultType.TOSTRING.getType().equals(printResultType)) {
                return e.getMessage();
            }
            if (PrintResultType.JSON.getType().equals(printResultType)) {
                return "{\n    \"result\": \"" + e.getMessage() + "\"\n}";
            }
            if (PrintResultType.DEBUG.getType().equals(printResultType)) {
                return "";
            }
            return "";

        }
    }

    public static List<RunResultDTO> resultDetail(Project project, String fieldOffset) {
        RunResultDetailReq req = new RunResultDetailReq();
        req.setOffsetPath(fieldOffset);
        String body = HttpUtil.post(DebugToolsSettingState.getInstance(project).getUrl(RESULT_DETAIL_URI), DebugToolsJsonUtils.toJsonStr(req), TIMEOUT);
        return DebugToolsJsonUtils.toRunResultDTOList(body);
    }

    public static AllClassLoaderRes allClassLoader(Project project) throws IOException, InterruptedException {
        String body = HttpUtil.get(DebugToolsSettingState.getInstance(project).getUrl(ALL_CLASS_LOADER_URI), TIMEOUT);
        return DebugToolsJsonUtils.toBean(body, AllClassLoaderRes.class);
    }

    public static String getApplicationName(Project project, boolean local) throws IOException, InterruptedException {
        return HttpUtil.get(DebugToolsSettingState.getInstance(project).getUrl(GET_APPLICATION_NAME_URI, local), TIMEOUT);
    }
}
