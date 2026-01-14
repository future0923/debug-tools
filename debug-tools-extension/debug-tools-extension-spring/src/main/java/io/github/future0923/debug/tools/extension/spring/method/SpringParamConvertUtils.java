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
package io.github.future0923.debug.tools.extension.spring.method;

import io.github.future0923.debug.tools.base.hutool.core.date.DateUtil;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsLambdaUtils;
import io.github.future0923.debug.tools.extension.spring.request.MockMultipartFile;
import io.github.future0923.debug.tools.server.utils.BeanInstanceUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class SpringParamConvertUtils {

    private static final Logger log = Logger.getLogger(SpringParamConvertUtils.class);

    private static final SimpleTypeConverter simpleTypeConverter = new SimpleTypeConverter();

    public static Object[] getArgs(Method bridgedMethod, Map<String, RunContentDTO> targetMethodContent) {
        SynthesizingMethodParameter[] parameters = new SynthesizingMethodParameter[bridgedMethod.getParameterCount()];
        for (int i = 0; i < bridgedMethod.getParameterCount(); i++) {
            parameters[i] = new SynthesizingMethodParameter(bridgedMethod, i);
        }
        Object[] targetMethodArgs = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            SynthesizingMethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            try {
                targetMethodArgs[i] = SpringParamConvertUtils.getArg(targetMethodContent, parameter);
            } catch (Exception e) {
                log.error("转换参数[{}]失败", e, parameter.getParameterName());
                targetMethodArgs[i] = null;
            }
        }
        return targetMethodArgs;
    }


    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getArg(Map<String, RunContentDTO> contentMap, SynthesizingMethodParameter parameter) {
        if (contentMap == null || contentMap.isEmpty()) {
            return null;
        }
        RunContentDTO runContentDTO = getRunContentDTO(contentMap, parameter);
        if (runContentDTO == null) {
            return null;
        }
        if (RunContentType.BEAN.getType().equals(runContentDTO.getType())) {
            return BeanInstanceUtils.getInstance(parameter.getParameterType());
        } else if (RunContentType.LAMBDA.getType().equals(runContentDTO.getType())) {
            if (runContentDTO.getContent() != null && parameter.getParameterType().isInterface() && (runContentDTO.getContent().toString().contains("->") || runContentDTO.getContent().toString().contains("::"))) {
                return DebugToolsLambdaUtils.createLambda(runContentDTO.getContent().toString(), parameter.getNestedGenericParameterType());
            }
        } else if (RunContentType.JSON_ENTITY.getType().equals(runContentDTO.getType())) {
            return DebugToolsJsonUtils.toBean(DebugToolsJsonUtils.toJsonStr(runContentDTO.getContent()), ResolvableType.forMethodParameter(parameter).getType(), true);
        } else if (RunContentType.SIMPLE.getType().equals(runContentDTO.getType())) {
            if (DebugToolsClassUtils.isSimpleValueType(parameter.getParameterType())) {
                try {
                    if (parameter.getParameterType().isAssignableFrom(LocalDateTime.class)) {
                        return LocalDateTime.parse(runContentDTO.getContent().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                    if (parameter.getParameterType().isAssignableFrom(LocalDate.class)) {
                        return LocalDate.parse(runContentDTO.getContent().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                    if (parameter.getParameterType().isAssignableFrom(LocalTime.class)) {
                        return LocalTime.parse(runContentDTO.getContent().toString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                    }
                    if (parameter.getParameterType().isAssignableFrom(Date.class)) {
                        return DateUtil.parse(runContentDTO.getContent().toString(), "yyyy-MM-dd HH:mm:ss");
                    }
                    // spring简单类型转换
                    return simpleTypeConverter.convertIfNecessary(runContentDTO.getContent(), parameter.getParameterType(), parameter);
                } catch (Exception e) {
                    log.error("转换失败", e);
                    return null;
                }
            }
        } else if (RunContentType.ENUM.getType().equals(runContentDTO.getType())) {
            // 处理数组格式的枚举参数（手动传入的数组）
            if (runContentDTO.getContent() instanceof Iterable) {
                List<Object> enumList = new ArrayList<>();
                Class<?> enumType;
                if (parameter.getParameterType().isArray()) {
                    enumType = parameter.getParameterType().getComponentType();
                } else {
                    // 处理泛型类型，如 List<SomeEnum>
                    enumType = ResolvableType.forMethodParameter(parameter).getGeneric().resolve();
                }
                if (enumType != null && enumType.isEnum()) {
                    for (Object item : (Iterable<?>) runContentDTO.getContent()) {
                        enumList.add(Enum.valueOf((Class<? extends Enum>) enumType, item.toString()));
                    }
                    if (parameter.getParameterType().isArray()) {
                        return enumList.toArray((Object[]) java.lang.reflect.Array.newInstance(enumType, enumList.size()));
                    }
                    return enumList;
                }
            }
            // 处理单个枚举
            return Enum.valueOf((Class<? extends Enum>) parameter.getParameterType(), runContentDTO.getContent().toString());
        } else if (RunContentType.REQUEST.getType().equals(runContentDTO.getType())) {
            if (parameter.getParameterType().getName().equals("javax.servlet.http.HttpServletRequest")) {
                return DebugToolsEnvUtils.getRequest();
            }
            if (parameter.getParameterType().getName().equals("org.springframework.http.server.reactive.ServerHttpRequest")) {
                return DebugToolsEnvUtils.getServerHttpRequest();
            }
            if (parameter.getParameterType().getName().equals("org.springframework.web.server.ServerWebExchange")) {
                return DebugToolsEnvUtils.getServerWebExchange();
            }
            return null;
        } else if (RunContentType.RESPONSE.getType().equals(runContentDTO.getType())) {
            if (parameter.getParameterType().getName().equals("javax.servlet.http.HttpServletResponse")) {
                return DebugToolsEnvUtils.getResponse();
            }
            if (parameter.getParameterType().getName().equals("org.springframework.http.server.reactive.ServerHttpResponse")) {
                return DebugToolsEnvUtils.getServerHttpResponse();
            }
            return null;
        } else if (RunContentType.FILE.getType().equals(runContentDTO.getType())) {
            // 处理数组格式的文件参数（手动传入的数组）
            if (runContentDTO.getContent() instanceof Iterable) {
                List<MultipartFile> multipartFiles = new ArrayList<>();
                for (Object item : (Iterable<?>) runContentDTO.getContent()) {
                    File file = new File(item.toString());
                    try {
                        String originalFilename = file.getName();
                        String contentType = FileUtil.getMimeType(file.getPath());
                        multipartFiles.add(new MockMultipartFile(originalFilename, originalFilename, contentType, FileUtil.getInputStream(file)));
                    } catch (IOException e) {
                        log.error("转换MockMultipartFile异常", e);
                    }
                }
                if (parameter.getParameterType().isArray() &&
                    MultipartFile.class.isAssignableFrom(parameter.getParameterType().getComponentType())) {
                    return multipartFiles.toArray(new MultipartFile[0]);
                }
                return multipartFiles;
            }

            // 处理单个文件
            File file = new File(runContentDTO.getContent().toString());
            // 处理单个MultipartFile
            if (MultipartFile.class.isAssignableFrom(parameter.getParameterType())) {
                try {
                    String originalFilename = file.getName();
                    String contentType = FileUtil.getMimeType(file.getPath());
                    return new MockMultipartFile(originalFilename, originalFilename, contentType, FileUtil.getInputStream(file));
                } catch (IOException e) {
                    log.error("转换MockMultipartFile异常", e);
                    return null;
                }
            }
            return file;
        } else if (RunContentType.CLASS.getType().equals(runContentDTO.getType())) {
            try {
                return Class.forName(runContentDTO.getContent().toString());
            } catch (Exception e) {
                log.error("转换Class异常", e);
            }
        }
        return null;
    }

    private static RunContentDTO getRunContentDTO(Map<String, RunContentDTO> contentMap, SynthesizingMethodParameter parameter) {
        String parameterName = parameter.getParameterName();
        if (DebugToolsStringUtils.isNotBlank(parameterName)) {
            return contentMap.get(parameterName);
        } else {
            int index = 0;
            for (RunContentDTO value : contentMap.values()) {
                if (parameter.getParameterIndex() == index++) {
                    return value;
                }
            }
        }
        return null;
    }
}
