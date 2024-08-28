package io.github.future0923.debug.power.idea.client.http;

import com.intellij.openapi.project.Project;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.protocal.http.RunResultDetailReq;
import io.github.future0923.debug.power.common.protocal.http.RunResultTypeReq;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.idea.setting.DebugPowerSettingState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * @author future0923
 */
public class HttpClientUtils {

    private static final HttpClient httpClient;

    private static final String RESULT_TYPE_URI = "/result/type";

    private static final String RESULT_DETAIL_URI = "/result/detail";

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
            return post(project, RESULT_TYPE_URI, DebugPowerJsonUtils.toJsonStr(req));
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
            String body = post(project, RESULT_DETAIL_URI, DebugPowerJsonUtils.toJsonStr(req));
            return DebugPowerJsonUtils.toRunResultDTOList(body);
        } catch (IOException | InterruptedException e) {
            return Collections.emptyList();
        }
    }

    public static String post(Project project, String uri, String jsonBody) throws IOException, InterruptedException {
        DebugPowerSettingState settingState = DebugPowerSettingState.getInstance(project);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://127.0.0.1:" + settingState.getHttpPort() + uri))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }
}
