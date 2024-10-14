package io.github.future0923.debug.tools.idea.client.http;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.PrintResultType;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.tools.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.setting.DebugToolsSettingState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class HttpClientUtils {

    private static final HttpClient httpClient;

    private static final String RESULT_TYPE_URI = "/result/type";

    private static final String RESULT_DETAIL_URI = "/result/detail";

    private static final String ALL_CLASS_LOADER_URI = "/allClassLoader";

    private static final String GET_APPLICATION_NAME_URI = "/getApplicationName";

    private static final Map<Project, AllClassLoaderRes> allClassLoaderResCache = new ConcurrentHashMap<>();

    static {
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(3))
                .build();
    }

    public static String resultType(Project project, String offsetPath, String printResultType) {
        RunResultTypeReq req = new RunResultTypeReq();
        req.setPrintResultType(printResultType);
        req.setOffsetPath(offsetPath);
        try {
            return post(project, RESULT_TYPE_URI, DebugToolsJsonUtils.toJsonStr(req));
        } catch (IOException | InterruptedException e) {
            if (PrintResultType.TOSTRING.getType().equals(printResultType)) {
                return e.getMessage();
            }
            if (PrintResultType.JSON.getType().equals(printResultType)) {
                return "{\n    \"result\": \"" + e.getMessage()+"\"\n}";
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
        try {
            String body = post(project, RESULT_DETAIL_URI, DebugToolsJsonUtils.toJsonStr(req));
            return DebugToolsJsonUtils.toRunResultDTOList(body);
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }
    }

    public static AllClassLoaderRes allClassLoader(Project project, boolean cache) {
        AllClassLoaderRes allClassLoaderRes = allClassLoaderResCache.get(project);
        if (allClassLoaderRes == null || !cache) {
            try {
                String body = post(project, ALL_CLASS_LOADER_URI, "{}");
                AllClassLoaderRes res = DebugToolsJsonUtils.toBean(body, AllClassLoaderRes.class);
                allClassLoaderResCache.put(project, res);
                return res;
            } catch (IOException | InterruptedException ignored) {
            }
        }
        return allClassLoaderRes;
    }

    public static void removeAllClassLoaderCache(Project project) {
        allClassLoaderResCache.remove(project);
    }

    public static String getApplicationName(Project project) throws IOException, InterruptedException {
        return post(project, GET_APPLICATION_NAME_URI, "{}");
    }

    public static String post(Project project, String uri, String jsonBody) throws IOException, InterruptedException {
        DebugToolsSettingState settingState = DebugToolsSettingState.getInstance(project);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + (settingState.isLocal() ? "127.0.0.1" : settingState.getRemoteHost()) + ":" + (settingState.isLocal() ? settingState.getLocalHttpPort() : settingState.getRemoteHttpPort()) + uri))
                .timeout(Duration.ofSeconds(3))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
