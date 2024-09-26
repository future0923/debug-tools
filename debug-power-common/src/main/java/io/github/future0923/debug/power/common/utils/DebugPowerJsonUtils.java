package io.github.future0923.debug.power.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunResultDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.protocal.http.AllClassLoaderRes;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugPowerJsonUtils extends JSONUtil {

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

    public static List<AllClassLoaderRes> toAllClassLoaderRes(String jsonInput) {
        return toBean(jsonInput, new TypeReference<List<AllClassLoaderRes>>() {
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
        if (DebugPowerStringUtils.isBlank(json) || "{}".equals(json)) {
            return json;
        }
        return toJsonStr(parse(json));
    }

    /**
     * 正常的json转为DebugPower能运行的json
     *
     * @param jsonInput 正常的json
     * @return DebugPower能运行的json
     */
    public static String jsonConvertDebugPowerJson(String jsonInput) {
        JSONObject jsonObject = parseObj(jsonInput);
        JSONObject result = DebugPowerJsonUtils.createJsonObject();
        for (Map.Entry<String, Object> entry : jsonObject) {
            String key = entry.getKey();
            Object value = entry.getValue();
            JSONObject runContent = DebugPowerJsonUtils.createJsonObject();
            if (DebugPowerClassUtils.isSimpleValueType(value.getClass())) {
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
     * DebugPower能运行的json转为正常的json
     *
     * @param jsonInput 正常的json
     * @return DebugPower能运行的json
     */
    public static String debugPowerJsonConvertJson(String jsonInput) {
        Map<String, RunContentDTO> runContentMap = toRunContentDTOMap(jsonInput);
        JSONObject result = DebugPowerJsonUtils.createJsonObject();
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
                    && DebugPowerStringUtils.isBlank(v.getContent().toString())) {
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
     * 正常的query转为DebugPower能运行的json
     *
     * @param queryStr query参数
     * @return DebugPower能运行的json
     */
    public static String queryConvertDebugPowerJson(String queryStr) {
        try {
            URI url = new URI(URLDecoder.decode(queryStr, StandardCharsets.UTF_8.name()));
            String query = url.getQuery() != null ? url.getQuery() : url.getPath();
            JSONObject result = DebugPowerJsonUtils.createJsonObject();
            Arrays.stream(query.split("&"))
                    .map(p -> p.split("="))
                    .filter(p -> p.length > 0)
                    .forEach(p -> {
                        JSONObject runContent = DebugPowerJsonUtils.createJsonObject();
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
     * DebugPower能运行的json转为正常的query
     *
     * @param jsonInput DebugPower能运行的json
     * @return query参数
     */
    public static String debugPowerJsonConvertQuery(String jsonInput) {
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
     * 正常的path转为DebugPower能运行的json
     *
     * @param pathStr        path参数
     * @param methodArgsName 方法SIMPLE参数名
     * @return DebugPower能运行的json
     */
    public static String pathConvertDebugPowerJson(String pathStr, List<String> methodArgsName) {
        if (DebugPowerStringUtils.isBlank(pathStr) || CollUtil.isEmpty(methodArgsName)) {
            return "{}";
        }
        String path = StrUtil.removeSuffix(StrUtil.removePrefix(pathStr, "/"), "/");
        JSONObject result = DebugPowerJsonUtils.createJsonObject();
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            if (i >= methodArgsName.size()) {
                break;
            }
            JSONObject runContent = DebugPowerJsonUtils.createJsonObject();
            runContent.set("type", RunContentType.SIMPLE.getType());
            runContent.set("content", split[i]);
            result.set(methodArgsName.get(i), runContent);
        }
        return toJsonPrettyStr(result);
    }

    /**
     * DebugPower能运行的json转为正常的path
     *
     * @param jsonInput DebugPower能运行的json
     * @return path参数
     */
    public static String debugPowerJsonConvertPath(String jsonInput) {
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
