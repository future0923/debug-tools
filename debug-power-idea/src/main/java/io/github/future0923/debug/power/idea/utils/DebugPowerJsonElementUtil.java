package io.github.future0923.debug.power.idea.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.impl.source.tree.PsiErrorElementImpl;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DebugPowerJsonElementUtil {

    public static String getContentType(PsiType type) {
        if (type.isAssignableFrom(PsiTypes.intType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.longType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.booleanType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.byteType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.charType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.doubleType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.floatType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type.isAssignableFrom(PsiTypes.shortType())) {
            return RunContentType.SIMPLE.getType();
        }
        if (type instanceof PsiArrayType) {
            return RunContentType.JSON_ENTITY.getType();
        }
        if (type instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) type).resolve();
            if (null != psiClass) {
                if (psiClass.isEnum()) {
                    return RunContentType.ENUM.getType();
                } else {
                    if (psiClass.getAnnotation("org.springframework.stereotype.Service") != null) {
                        return RunContentType.BEAN.getType();
                    }
                    if (psiClass.getAnnotation("org.springframework.stereotype.Component") != null) {
                        return RunContentType.BEAN.getType();
                    }
                    if (psiClass.getAnnotation("org.springframework.stereotype.Controller") != null) {
                        return RunContentType.BEAN.getType();
                    }
                    if (psiClass.getAnnotation("org.springframework.stereotype.Repository") != null) {
                        return RunContentType.BEAN.getType();
                    }
                    if (psiClass.isInterface() && psiClass.getAnnotation("java.lang.FunctionalInterface") != null) {
                        return RunContentType.LAMBDA.getType();
                    }
                    try {
                        Class<?> aClass = Class.forName(psiClass.getQualifiedName());
                        if (isSimpleValueType(aClass)) {
                            return RunContentType.SIMPLE.getType();
                        }
                    } catch (Exception ignored) {
                    }
                    return RunContentType.JSON_ENTITY.getType();
                }
            }
            return RunContentType.JSON_ENTITY.getType();
        }
        return RunContentType.UNKNOWN.getType();
    }


    public static JsonElement toJson(PsiType type) {
        // 新版本使用：PsiTypes.intType()
        if (type.isAssignableFrom(PsiTypes.intType())) {
            return new JsonPrimitive(0);
        }
        if (type.isAssignableFrom(PsiTypes.longType())) {
            return new JsonPrimitive(0);
        }
        if (type.isAssignableFrom(PsiTypes.booleanType())) {
            return new JsonPrimitive(false);
        }
        if (type.isAssignableFrom(PsiTypes.byteType())) {
            return new JsonPrimitive("");
        }
        if (type.isAssignableFrom(PsiTypes.charType())) {
            return new JsonPrimitive("");
        }
        if (type.isAssignableFrom(PsiTypes.doubleType())) {
            return new JsonPrimitive(0.00);
        }
        if (type.isAssignableFrom(PsiTypes.floatType())) {
            return new JsonPrimitive(0.0);
        }
        if (type.isAssignableFrom(PsiTypes.shortType())) {
            return new JsonPrimitive("");
        }
        if (type instanceof PsiArrayType) {
            JsonArray jsonElements = new JsonArray();
            jsonElements.add(toJson(((PsiArrayType) type).getComponentType()));
            return jsonElements;
        }
        if (type instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) type).resolve();
            if (null != psiClass) {
                if (psiClass.isEnum()) {
                    PsiField[] fields = psiClass.getFields();
                    if (fields.length > 0) {
                        return new JsonPrimitive(fields[0].getName());
                    } else {
                        return new JsonPrimitive("");
                    }
                } else {
                    try {
                        Class<?> aClass = Class.forName(psiClass.getQualifiedName());
                        if (isSimpleValueType(aClass)) {
                            return new JsonPrimitive("");
                        }
                        if (isCollType(aClass)) {
                            JsonArray jsonElements = new JsonArray();
                            Arrays.stream(((PsiClassType) type).getParameters()).map(DebugPowerJsonElementUtil::toJson).forEach(jsonElements::add);
                            return jsonElements;
                        }
                        if (isMapType(aClass)) {
                            JsonObject jsonObject = new JsonObject();
                            PsiType[] parameters = ((PsiClassType) type).getParameters();
                            if (parameters.length > 1) {
                                JsonElement key = toJson(parameters[0]);
                                JsonElement value = toJson(parameters[1]);
                                jsonObject.add(key instanceof JsonPrimitive ? key.getAsString() : key.toString(), value);
                            }
                            return jsonObject;
                        }
                    } catch (Exception ignored) {
                    }
                    if (psiClass.isInterface()) {
                        return JsonNull.INSTANCE;
                    }
                    JsonObject jsonObject1 = new JsonObject();
                    Arrays.stream(psiClass.getFields()).forEach(field -> {
                        if (!StringUtils.contains(field.getText(), " static ")) {
                            jsonObject1.add(field.getName(), toJson(field.getType()));
                        }
                    });
                    return jsonObject1;
                }
            }
        }


        return JsonNull.INSTANCE;
    }

    public static boolean isSimpleValueType(Class<?> type) {
        return (Void.class != type && void.class != type &&
                (ClassUtils.isPrimitiveOrWrapper(type) ||
                        Enum.class.isAssignableFrom(type) ||
                        CharSequence.class.isAssignableFrom(type) ||
                        Number.class.isAssignableFrom(type) ||
                        Date.class.isAssignableFrom(type) ||
                        Temporal.class.isAssignableFrom(type) ||
                        URI.class == type ||
                        URL.class == type ||
                        Locale.class == type ||
                        Class.class == type));
    }

    public static boolean isCollType(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public static String getJsonText(PsiParameterList psiParameterList1) {
        JsonObject jsonObject = toParamNameListNew(psiParameterList1);
        return DebugPowerJsonUtils.getInstancePretty().toJson(jsonObject);
    }

    public static JsonObject toParamNameListNew(PsiParameterList parameterList) {
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(parameterList.getParameter(i));
            String key = parameter.getName();
            PsiType type = parameter.getType();
            JsonObject argContent = new JsonObject();
            String contentType = getContentType(type);
            argContent.addProperty("type", contentType);
            if (!RunContentType.BEAN.getType().equals(contentType)) {
                argContent.add("content", toJson(type));
            }
            jsonObject.add(key, argContent);
        }
        return jsonObject;
    }

    public static String getSimpleText(PsiParameterList parameterList) {
        JsonObject jsonObject = new JsonObject();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(parameterList.getParameter(i));
            String key = parameter.getName();
            jsonObject.add(key, null);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
        return gson.toJson(jsonObject);
    }

    /**
     * 获取JsonObject的所有key
     */
    public static Set<String> getJsonObjectKey(JsonObject jsonObject) {
        Set<String> keys = new HashSet<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            keys.add(key);
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                keys.addAll(getJsonObjectKey(value.getAsJsonObject()));
            }
        }
        return keys;
    }

    public static boolean isJsonKey(PsiElement psiElement) {
        if (psiElement == null) {
            return false;
        }
        if (psiElement instanceof LeafPsiElement && psiElement.getParent() != null) {
            psiElement = psiElement.getParent();
        }
        PsiElement nextSibling = psiElement.getNextSibling();
        while (nextSibling instanceof PsiWhiteSpace) {
            nextSibling = nextSibling.getNextSibling();
        }
        // 当前位置后面有冒号的
        boolean isJsonKey = nextSibling != null && nextSibling.getText().equals(":");
        if (isJsonKey) {
            return true;
        }
        if (nextSibling instanceof PsiErrorElementImpl) {
            return ((PsiErrorElementImpl) nextSibling).getErrorDescription().contains("':' expected");
        }
        PsiElement psiElementParent = psiElement;
        while (psiElementParent.getParent() != null) {
            psiElementParent = psiElementParent.getParent();
        }
        if (isJsonKey(psiElementParent.getText(), psiElement.getTextOffset())) {
            return true;
        }
        return false;
    }

    public static boolean isJsonKey(String json, int index) {
        if (json == null || json.isBlank()) {
            return false;
        }
        if (index >= json.length()) {
            return false;
        }

        // 当前位置前面有,并且是在:之后的
        int colonIndex = json.lastIndexOf(':', index); // 查找当前索引之前的最后一个冒号
        int commaIndex = json.lastIndexOf(',', index); // 查找当前索引之前的最后一个冒号
        if (colonIndex < commaIndex && commaIndex < index) {
            return true;
        }
        // 当前位置的前一个非空字符是 { 123
        char preChar = '\0';
        for (int i = index - 1; i >= 0; i--) {
            char ch = json.charAt(i);
            if (!Character.isWhitespace(ch)) {
                preChar = ch; // 返回找到的非空格字符
                break;
            }
        }
        return 123 == preChar;
    }

    public static JsonArray toJsonArray(List<String> list) {
        JsonArray jsonArray = new JsonArray();
        list.forEach(jsonArray::add);
        return jsonArray;
    }
}
