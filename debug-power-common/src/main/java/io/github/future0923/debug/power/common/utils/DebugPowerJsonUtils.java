package io.github.future0923.debug.power.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;

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
}
