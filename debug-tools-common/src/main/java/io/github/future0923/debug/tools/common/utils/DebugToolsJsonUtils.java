package io.github.future0923.debug.tools.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.RunContentType;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugToolsJsonUtils extends JSONUtil {

    public static final JSONConfig JSON_CONFIG;

    static {
        JSON_CONFIG = JSONConfig.create()
                .setDateFormat(DatePattern.NORM_DATETIME_MS_PATTERN)
                .setIgnoreNullValue(false);
    }

    public static JSONObject createJsonObject() {
        return new JSONObject(JSON_CONFIG);
    }

    public static Map<String, RunContentDTO> toRunContentDTOMap(String jsonInput) {
        return toBean(jsonInput, new TypeReference<Map<String, RunContentDTO>>() {
        }, true);
    }

    public static List<RunResultDTO> toRunResultDTOList(String jsonInput) {
        return toBean(jsonInput, new TypeReference<List<RunResultDTO>>() {
        }, true);
    }

    /**
     * 美化json
     *
     * @param jsonStr 原始json
     * @return 美化后的json
     */
    public static String pretty(String jsonStr) {
        return toJsonPrettyStr(parse(jsonStr));
    }

    /**
     * 压缩json
     *
     * @param json 原始json
     * @return 压缩后的json
     */
    public static String compress(String json) {
        if (DebugToolsStringUtils.isBlank(json) || "{}".equals(json)) {
            return json;
        }
        return toJsonStr(parse(json));
    }

    /**
     * 正常的json转为DebugTools能运行的json
     *
     * @param jsonInput 正常的json
     * @return DebugTools能运行的json
     */
    public static String jsonConvertDebugToolsJson(String jsonInput) {
        JSONObject jsonObject = parseObj(jsonInput);
        JSONObject result = DebugToolsJsonUtils.createJsonObject();
        for (Map.Entry<String, Object> entry : jsonObject) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JSONObject runContent = DebugToolsJsonUtils.createJsonObject();
            if (DebugToolsClassUtils.isSimpleValueType(value.getClass())) {
                runContent.set("type", RunContentType.SIMPLE.getType());
            } else {
                runContent.set("type", RunContentType.JSON_ENTITY.getType());
            }
            runContent.set("content", value);
            result.set(key, runContent);
        }
        return toJsonPrettyStr(result);
    }

    /**
     * DebugTools能运行的json转为正常的json
     *
     * @param jsonInput 正常的json
     * @return DebugTools能运行的json
     */
    public static String debugToolsJsonConvertJson(String jsonInput) {
        Map<String, RunContentDTO> runContentMap = toRunContentDTOMap(jsonInput);
        JSONObject result = DebugToolsJsonUtils.createJsonObject();
        for (Map.Entry<String, RunContentDTO> entry : runContentMap.entrySet()) {
            String k = entry.getKey();
            RunContentDTO v = entry.getValue();
            if (runContentMap.size() == 1 && RunContentType.JSON_ENTITY.getType().equals(v.getType())) {
                return toJsonPrettyStr(parse(v.getContent()));
            }
            if (v.getContent() == null) {
                result.set(k, JSONNull.NULL);
                continue;
            }
            if (RunContentType.SIMPLE.getType().equals(v.getType())
                    && DebugToolsStringUtils.isBlank(v.getContent().toString())) {
                result.set(k, "");
                continue;
            }
            if (RunContentType.SIMPLE.getType().equals(v.getType())) {
                result.set(k, v.getContent());
            } else if (RunContentType.JSON_ENTITY.getType().equals(v.getType())) {
                result.set(k, parse(v.getContent()));
            }
        }
        return toJsonPrettyStr(result);
    }

    /**
     * 正常的query转为DebugTools能运行的json
     *
     * @param queryStr query参数
     * @return DebugTools能运行的json
     */
    public static String queryConvertDebugToolsJson(String queryStr) {
        try {
            URI url = new URI(URLDecoder.decode(queryStr, StandardCharsets.UTF_8.name()));
            String query = url.getQuery() != null ? url.getQuery() : url.getPath();
            JSONObject result = DebugToolsJsonUtils.createJsonObject();
            Arrays.stream(query.split("&"))
                    .map(p -> p.split("="))
                    .filter(p -> p.length > 0)
                    .forEach(p -> {
                        JSONObject runContent = DebugToolsJsonUtils.createJsonObject();
                        runContent.set("type", RunContentType.SIMPLE.getType());
                        if (p.length == 2) {
                            runContent.set("content", p[1]);
                        } else {
                            runContent.set("content", JSONNull.NULL);
                        }
                        result.set(p[0], runContent);
                    });
            return toJsonPrettyStr(result);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * DebugTools能运行的json转为正常的query
     *
     * @param jsonInput DebugTools能运行的json
     * @return query参数
     */
    public static String debugToolsJsonConvertQuery(String jsonInput) {
        Map<String, RunContentDTO> runContentMap = toRunContentDTOMap(jsonInput);
        StringBuilder sb = new StringBuilder();
        runContentMap.forEach((k, v) -> {
            if (RunContentType.SIMPLE.getType().equals(v.getType())) {
                sb.append(k).append("=");
                if (v.getContent() != null) {
                    sb.append(v.getContent());
                }
                sb.append("&");
            }
        });
        return StrUtil.removeSuffix(sb.toString(), "&");
    }

    /**
     * 正常的path转为DebugTools能运行的json
     *
     * @param pathStr        path参数
     * @param methodArgsName 方法SIMPLE参数名
     * @return DebugTools能运行的json
     */
    public static String pathConvertDebugToolsJson(String pathStr, List<String> methodArgsName) {
        if (DebugToolsStringUtils.isBlank(pathStr) || CollUtil.isEmpty(methodArgsName)) {
            return "{}";
        }
        String path = StrUtil.removeSuffix(StrUtil.removePrefix(pathStr, "/"), "/");
        JSONObject result = DebugToolsJsonUtils.createJsonObject();
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            if (i >= methodArgsName.size()) {
                break;
            }
            JSONObject runContent = DebugToolsJsonUtils.createJsonObject();
            runContent.set("type", RunContentType.SIMPLE.getType());
            runContent.set("content", split[i]);
            result.set(methodArgsName.get(i), runContent);
        }
        return toJsonPrettyStr(result);
    }

    /**
     * DebugTools能运行的json转为正常的path
     *
     * @param jsonInput DebugTools能运行的json
     * @return path参数
     */
    public static String debugToolsJsonConvertPath(String jsonInput) {
        Map<String, RunContentDTO> runContentMap = toRunContentDTOMap(jsonInput);
        StringBuilder sb = new StringBuilder();
        runContentMap.forEach((k, v) -> {
            if (RunContentType.SIMPLE.getType().equals(v.getType()) && v.getContent() != null) {
                sb.append("/").append(v.getContent());
            }
        });
        return sb.toString();
    }

    /**
     * 转换为格式化后的JSON字符串
     *
     * @param obj Bean对象
     * @return JSON字符串
     */
    public static String toJsonPrettyStr(Object obj) {
        return JSONUtil.toJsonPrettyStr(parse(obj, JSON_CONFIG));
    }
}
