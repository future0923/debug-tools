package io.github.future0923.debug.tools.server.mock.spring.method;

import cn.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.enums.RunContentType;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.common.utils.DebugToolsLambdaUtils;
import io.github.future0923.debug.tools.server.jvm.VmToolsUtils;
import io.github.future0923.debug.tools.server.mock.spring.request.MockMultipartFile;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.Date;
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
            return VmToolsUtils.getSpringInstance(parameter.getParameterType());
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
                        return DateUtils.parseDate(runContentDTO.getContent().toString(), "yyyy-MM-dd HH:mm:ss");
                    }
                    // spring简单类型转换
                    return simpleTypeConverter.convertIfNecessary(runContentDTO.getContent(), parameter.getParameterType(), parameter);
                } catch (Exception e) {
                    log.error("转换失败", e);
                    return null;
                }
            }
        } else if (RunContentType.ENUM.getType().equals(runContentDTO.getType())) {
            return Enum.valueOf((Class<? extends Enum>) parameter.getParameterType(), runContentDTO.getContent().toString());
        } else if (RunContentType.REQUEST.getType().equals(runContentDTO.getType())) {
            return DebugToolsEnvUtils.getRequest();
        } else if (RunContentType.RESPONSE.getType().equals(runContentDTO.getType())) {
            return DebugToolsEnvUtils.getResponse();
        } else if (RunContentType.FILE.getType().equals(runContentDTO.getType())) {
            File file = new File(runContentDTO.getContent().toString());
            if (MultipartFile.class.isAssignableFrom(parameter.getParameterType())) {
                try {
                    return new MockMultipartFile(file.getName(), FileUtil.getInputStream(file));
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
