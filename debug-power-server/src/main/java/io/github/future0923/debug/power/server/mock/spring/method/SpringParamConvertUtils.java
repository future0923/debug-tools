package io.github.future0923.debug.power.server.mock.spring.method;

import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerLambdaUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerSpringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.SynthesizingMethodParameter;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            targetMethodArgs[i] = SpringParamConvertUtils.getArg(targetMethodContent, parameter);
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
            return DebugPowerSpringUtils.getBean(parameter.getParameterType());
        } else if (RunContentType.LAMBDA.getType().equals(runContentDTO.getType())) {
            if (runContentDTO.getContent() != null && parameter.getParameterType().isInterface() && (runContentDTO.getContent().toString().contains("->") || runContentDTO.getContent().toString().contains("::"))) {
                return DebugPowerLambdaUtils.createLambda(runContentDTO.getContent().toString(), parameter.getNestedGenericParameterType());
            }
        } else if (RunContentType.JSON_ENTITY.getType().equals(runContentDTO.getType())) {
            return DebugPowerJsonUtils.toBean(DebugPowerJsonUtils.toJsonStr(runContentDTO.getContent()), ResolvableType.forMethodParameter(parameter).getType(), true);
        } else if (RunContentType.SIMPLE.getType().equals(runContentDTO.getType())) {
            if (DebugPowerClassUtils.isSimpleValueType(parameter.getParameterType())) {
                try {
                    if (parameter.getParameterType().isAssignableFrom(LocalDateTime.class)) {
                        return LocalDateTime.parse(runContentDTO.getContent().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    }
                    if (parameter.getParameterType().isAssignableFrom(LocalDate.class)) {
                        return LocalDate.parse(runContentDTO.getContent().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
        }
        return null;
    }

    private static RunContentDTO getRunContentDTO(Map<String, RunContentDTO> contentMap, SynthesizingMethodParameter parameter) {
        String parameterName = parameter.getParameterName();
        if (DebugPowerStringUtils.isNotBlank(parameterName)) {
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
