package io.github.future0923.debug.power.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugPowerJsonUtils {

    private static final Gson gson;
    private static final Gson gsonPretty;

    static {
        gson = new Gson();
        gsonPretty = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    }

    public static Gson getInstance() {
        return gson;
    }

    public static Gson getInstancePretty() {
        return gsonPretty;
    }

    public static String pretty(String jsonStr) {
        return gsonPretty.toJson(JsonParser.parseString(jsonStr));
    }

    public static String compress(String json) {
        if (StringUtils.isBlank(json) || "{}".equals(json)) {
            return json;
        }
        return gson.toJson(JsonParser.parseString(json));
    }

    /**
     * 正常的json转为DebugPower能运行的json
     *
     * @param jsonInput 正常的json
     * @return DebugPower能运行的json
     */
    public static String jsonConvertDebugPowerJson(String jsonInput) {
        JsonObject jsonObject = gson.fromJson(jsonInput, JsonObject.class);
        JsonObject result = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            JsonObject runContent = new JsonObject();
            if (value.isJsonPrimitive()) {
                runContent.addProperty("type", RunContentType.SIMPLE.getType());
            } else if (value.isJsonArray() || value.isJsonObject() || value.isJsonNull()) {
                runContent.addProperty("type", RunContentType.JSON_ENTITY.getType());
            } else {
                runContent.addProperty("type", RunContentType.UNKNOWN.getType());
            }
            runContent.add("content", value);
            result.add(key, runContent);
        }
        return gsonPretty.toJson(result);
    }

    /**
     * DebugPower能运行的json转为正常的json
     *
     * @param jsonInput 正常的json
     * @return DebugPower能运行的json
     */
    public static String debugPowerJsonConvertJson(String jsonInput) {
        Map<String, RunContentDTO> runContentMap = gson.fromJson(jsonInput, new TypeToken<Map<String, RunContentDTO>>() {
        }.getType());
        JsonObject result = new JsonObject();
        runContentMap.forEach((k, v) -> {
            if (v.getContent() == null) {
                result.add(k, JsonNull.INSTANCE);
                return;
            }
            if (RunContentType.SIMPLE.getType().equals(v.getType())
                    && StringUtils.isBlank(v.getContent().toString())) {
                result.add(k, new JsonPrimitive(""));
                return;
            }
            if (RunContentType.SIMPLE.getType().equals(v.getType())
                    || RunContentType.JSON_ENTITY.getType().equals(v.getType())) {
                result.add(k, JsonParser.parseString(v.getContent().toString()));
            }
        });
        return gsonPretty.toJson(result);
    }

    /**
     * 正常的query转为DebugPower能运行的json
     *
     * @param queryStr query参数
     * @return DebugPower能运行的json
     */
    public static String queryConvertDebugPowerJson(String queryStr) {
        try {
            URI url = new URI(URLDecoder.decode(queryStr, StandardCharsets.UTF_8));
            String query = url.getQuery() != null ? url.getQuery() : url.getPath();
            JsonObject result = new JsonObject();
            Arrays.stream(query.split("&"))
                    .map(p -> p.split("="))
                    .filter(p -> p.length > 0)
                    .forEach(p -> {
                        JsonObject runContent = new JsonObject();
                        runContent.addProperty("type", RunContentType.SIMPLE.getType());
                        if (p.length == 2) {
                            runContent.addProperty("content", p[1]);
                        } else {
                            runContent.add("content", JsonNull.INSTANCE);
                        }
                        result.add(p[0], runContent);
                    });
            return gsonPretty.toJson(result);
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
        Map<String, RunContentDTO> runContentMap = gson.fromJson(jsonInput, new TypeToken<Map<String, RunContentDTO>>() {
        }.getType());
        StringBuilder sb = new StringBuilder();
        runContentMap.forEach((k, v) -> {
            if (RunContentType.SIMPLE.getType().equals(v.getType())) {
                sb.append(k).append("=");
                if (v.getContent()!= null) {
                    sb.append(v.getContent());
                }
                sb.append("&");
            }
        });
        return StringUtils.removeEnd(sb.toString(), "&");
    }
}
