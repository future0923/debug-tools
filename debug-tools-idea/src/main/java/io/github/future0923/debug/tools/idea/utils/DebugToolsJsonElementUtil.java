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
package io.github.future0923.debug.tools.idea.utils;

import io.github.future0923.debug.tools.base.hutool.json.JSONArray;
import io.github.future0923.debug.tools.base.hutool.json.JSONConfig;
import io.github.future0923.debug.tools.base.hutool.json.JSONNull;
import io.github.future0923.debug.tools.base.hutool.json.JSONObject;
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
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.idea.setting.GenParamType;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DebugToolsJsonElementUtil {

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
            PsiType componentType = ((PsiArrayType) type).getComponentType();
            // 检查数组元素类型是否为MultipartFile
            if (componentType instanceof PsiClassType) {
                PsiClass componentClass = ((PsiClassType) componentType).resolve();
                if (componentClass != null && "org.springframework.web.multipart.MultipartFile".equals(componentClass.getQualifiedName())) {
                    return RunContentType.FILE.getType();
                }
            }
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
                    if (psiClass.isInterface() &&
                            ("javax.servlet.http.HttpServletRequest".equals(psiClass.getQualifiedName())
                                    || "org.springframework.http.server.reactive.ServerHttpRequest".equals(psiClass.getQualifiedName())
                                    || "org.springframework.web.server.ServerWebExchange".equals(psiClass.getQualifiedName()))) {
                        return RunContentType.REQUEST.getType();
                    }
                    if (psiClass.isInterface() &&
                            ("javax.servlet.http.HttpServletResponse".equals(psiClass.getQualifiedName())
                                    || "org.springframework.http.server.reactive.ServerHttpResponse".equals(psiClass.getQualifiedName()))) {
                        return RunContentType.RESPONSE.getType();
                    }
                    if (psiClass.isInterface() && "org.springframework.web.multipart.MultipartFile".equals(psiClass.getQualifiedName())) {
                        return RunContentType.FILE.getType();
                    }
                    try {
                        Class<?> aClass = Class.forName(psiClass.getQualifiedName());
                        if (isSimpleValueType(aClass)) {
                            return RunContentType.SIMPLE.getType();
                        } else if (aClass.isAssignableFrom(File.class)) {
                            return RunContentType.FILE.getType();
                        } else if (aClass.isAssignableFrom(Class.class)) {
                            return RunContentType.CLASS.getType();
                        } else if (isCollType(aClass)) {
                            // 检查集合的泛型参数是否为枚举类型
                            PsiType[] parameters = ((PsiClassType) type).getParameters();
                            if (parameters.length > 0) {
                                PsiType genericType = parameters[0];
                                if (genericType instanceof PsiClassType) {
                                    PsiClass genericClass = ((PsiClassType) genericType).resolve();
                                    if (genericClass != null && genericClass.isEnum()) {
                                        return RunContentType.ENUM.getType();
                                    }
                                }
                            }
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


    public static Object toJson(PsiType type, GenParamType genParamType, DeepHolder deep) {
        // 新版本使用：PsiTypes.intType()
        if (type.isAssignableFrom(PsiTypes.intType())) {
            return 0;
        }
        if (type.isAssignableFrom(PsiTypes.longType())) {
            return 0;
        }
        if (type.isAssignableFrom(PsiTypes.booleanType())) {
            return false;
        }
        if (type.isAssignableFrom(PsiTypes.byteType())) {
            return "";
        }
        if (type.isAssignableFrom(PsiTypes.charType())) {
            return "";
        }
        if (type.isAssignableFrom(PsiTypes.doubleType())) {
            return 0.00D;
        }
        if (type.isAssignableFrom(PsiTypes.floatType())) {
            return 0.0F;
        }
        if (type.isAssignableFrom(PsiTypes.shortType())) {
            return 0;
        }
        if (type instanceof PsiArrayType) {
            PsiType componentType = ((PsiArrayType) type).getComponentType();
            // 对于MultipartFile[]数组，返回空字符串作为文件路径占位符
            if (componentType instanceof PsiClassType) {
                PsiClass componentClass = ((PsiClassType) componentType).resolve();
                if (componentClass != null && "org.springframework.web.multipart.MultipartFile".equals(componentClass.getQualifiedName())) {
                    return new JSONArray(); // 返回空数组作为初始值
                }
            }
            // 其他类型的数组保持原有逻辑
            JSONArray jsonElements = new JSONArray();
            if (!deep.isMaxDeep()) {
                jsonElements.add(toJson(componentType, genParamType, deep.increment()));
            }
            return jsonElements;
        }
        if (type instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) type).resolve();
            if (null != psiClass) {
                if (psiClass.isEnum()) {
                    PsiField[] fields = psiClass.getFields();
                    if (fields.length > 0) {
                        return fields[0].getName();
                    } else {
                        return "";
                    }
                } else {
                    try {
                        Class<?> aClass = Class.forName(psiClass.getQualifiedName());
                        if (isSimpleValueType(aClass)) {
                            if (aClass.isAssignableFrom(LocalDateTime.class) || aClass.isAssignableFrom(Date.class)) {
                                return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                            } else if (aClass.isAssignableFrom(LocalDate.class)) {
                                return DateFormatUtils.format(new Date(), "yyyy-MM-dd");
                            } else {
                                return "";
                            }
                        }
                        if (aClass.isAssignableFrom(Class.class)) {
                            return "";
                        }
                        if (aClass.isAssignableFrom(File.class)) {
                            return "";
                        }
                        if (isCollType(aClass)) {
                            JSONArray jsonElements = new JSONArray();
                            if (!deep.isMaxDeep()) {
                                DeepHolder increment = deep.increment();
                                Arrays.stream(((PsiClassType) type).getParameters()).map(psiType -> toJson(psiType, genParamType, increment)).forEach(jsonElements::add);
                            }
                            return jsonElements;
                        }
                        if (isMapType(aClass)) {
                            JSONObject jsonObject = DebugToolsJsonUtils.createJsonObject();
                            PsiType[] parameters = ((PsiClassType) type).getParameters();
                            if (parameters.length > 1 && !deep.isMaxDeep()) {
                                DeepHolder increment = deep.increment();
                                Object key = toJson(parameters[0], genParamType, increment);
                                Object value = toJson(parameters[1], genParamType, increment);
                                if (key != null) {
                                    jsonObject.set(key.toString(), value);
                                }
                            }
                            return jsonObject;
                        }
                    } catch (Exception ignored) {
                    }
                    if (psiClass.isInterface()) {
                        return JSONNull.NULL;
                    }
                    JSONObject jsonObject1 = DebugToolsJsonUtils.createJsonObject();
                    if (GenParamType.SIMPLE.equals(genParamType)) {
                        return jsonObject1;
                    } else if (!deep.isMaxDeep()) {
                        PsiField[] fields;
                        if (GenParamType.CURRENT.equals(genParamType)) {
                            fields = psiClass.getFields();
                        } else if (GenParamType.ALL.equals(genParamType)) {
                            fields = psiClass.getAllFields();
                        } else {
                            fields = psiClass.getFields();
                        }
                        DeepHolder increment = deep.increment();
                        Arrays.stream(fields).forEach(field -> {
                            if (!StringUtils.contains(field.getText(), " static ")) {
                                jsonObject1.set(field.getName(), toJson(field.getType(), genParamType, increment));
                            }
                        });
                    }
                    return jsonObject1;
                }
            }
        }
        return JSONNull.NULL;
    }

    public static Object toSimpleJson(PsiType type) {
        // 新版本使用：PsiTypes.intType()
        if (type.isAssignableFrom(PsiTypes.intType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.longType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.booleanType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.byteType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.charType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.doubleType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.floatType())) {
            return JSONNull.NULL;
        }
        if (type.isAssignableFrom(PsiTypes.shortType())) {
            return JSONNull.NULL;
        }
        if (type instanceof PsiArrayType) {
            PsiType componentType = ((PsiArrayType) type).getComponentType();
            // 对于MultipartFile[]数组，返回空数组
            if (componentType instanceof PsiClassType) {
                PsiClass componentClass = ((PsiClassType) componentType).resolve();
                if (componentClass != null && "org.springframework.web.multipart.MultipartFile".equals(componentClass.getQualifiedName())) {
                    return new JSONArray(); // 返回空数组
                }
            }
            return new JSONArray();
        }
        if (type instanceof PsiClassType) {
            PsiClass psiClass = ((PsiClassType) type).resolve();
            if (null != psiClass) {
                if (psiClass.isEnum()) {
                    return JSONNull.NULL;
                } else {
                    try {
                        Class<?> aClass = Class.forName(psiClass.getQualifiedName());
                        if (isSimpleValueType(aClass)) {
                            return JSONNull.NULL;
                        }
                        if (isCollType(aClass)) {
                            return new JSONArray();
                        }
                        if (isMapType(aClass)) {
                            return DebugToolsJsonUtils.createJsonObject();
                        }
                    } catch (Exception ignored) {
                    }
                    if (psiClass.isInterface()) {
                        return JSONNull.NULL;
                    }
                    return DebugToolsJsonUtils.createJsonObject();
                }
            }
        }
        return JSONNull.NULL;
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
                        Locale.class == type));
    }

    public static boolean isCollType(Class<?> type) {
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public static boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public static String getJsonText(PsiParameterList psiParameterList, GenParamType genParamType) {
        JSONObject jsonObject = toParamNameListNew(psiParameterList, genParamType);
        return DebugToolsJsonUtils.toJsonPrettyStr(jsonObject);
    }

    public static JSONObject toParamNameListNew(PsiParameterList parameterList, GenParamType genParamType) {
        JSONObject jsonObject = DebugToolsJsonUtils.createJsonObject();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(parameterList.getParameter(i));
            String key = parameter.getName();
            PsiType type = parameter.getType();
            JSONObject argContent = DebugToolsJsonUtils.createJsonObject();
            String contentType = getContentType(type);
            argContent.set("type", contentType);
            if (!RunContentType.BEAN.getType().equals(contentType)) {
                argContent.set("content", toJson(type, genParamType, new DeepHolder()));
            }
            jsonObject.set(key, argContent);
        }
        return jsonObject;
    }

    public static String getSimpleText(PsiParameterList parameterList) {
        JSONConfig jsonConfig = JSONConfig.create();
        jsonConfig.setIgnoreNullValue(false);
        JSONObject jsonObject = new JSONObject(jsonConfig);
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = Objects.requireNonNull(parameterList.getParameter(i));
            JSONObject argContent = new JSONObject(jsonConfig);
            argContent.set("type", getContentType(parameter.getType()));
            argContent.set("content", toSimpleJson(parameter.getType()));
            jsonObject.set(parameter.getName(), argContent);
        }
        return DebugToolsJsonUtils.toJsonPrettyStr(jsonObject);
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

    public static class DeepHolder {

        private int deep;

        public DeepHolder() {
            this.deep = 1;
        }

        public boolean isMaxDeep() {
            return deep >= 5;
        }

        public DeepHolder increment() {
            ++deep;
            return this;
        }
    }

}
