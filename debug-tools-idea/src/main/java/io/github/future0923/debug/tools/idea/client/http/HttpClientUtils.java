package io.github.future0923.debug.tools.idea.client.http;

import cn.hutool.http.HttpUtil;
import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.tools.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class HttpClientUtils {

    private static final String RESULT_TYPE_URI = "/result/type";

    private static final String RESULT_DETAIL_URI = "/result/detail";

    private static final String ALL_CLASS_LOADER_URI = "/allClassLoader";

    private static final String GET_APPLICATION_NAME_URI = "/getApplicationName";

    private static final int TIMEOUT = 5000;

    private static final Map<Project, AllClassLoaderRes> allClassLoaderResCache = new ConcurrentHashMap<>();

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

    public static AllClassLoaderRes allClassLoader(Project project, boolean cache) throws IOException, InterruptedException {
        AllClassLoaderRes allClassLoaderRes = allClassLoaderResCache.get(project);
        if (allClassLoaderRes == null || !cache) {
            String body = HttpUtil.get(DebugToolsSettingState.getInstance(project).getUrl(ALL_CLASS_LOADER_URI), TIMEOUT);
            AllClassLoaderRes res = DebugToolsJsonUtils.toBean(body, AllClassLoaderRes.class);
            allClassLoaderResCache.put(project, res);
            return res;
        }
        return allClassLoaderRes;
    }

    public static void removeAllClassLoaderCache(Project project) {
        allClassLoaderResCache.remove(project);
    }

    public static String getApplicationName(Project project, boolean local) throws IOException, InterruptedException {
        return HttpUtil.get(DebugToolsSettingState.getInstance(project).getUrl(GET_APPLICATION_NAME_URI, local), TIMEOUT);
    }
}
