package io.github.future0923.debug.power.common.utils;

import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.enums.RunContentType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.SynthesizingMethodParameter;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugPowerParamConvertUtils {

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
            targetMethodArgs[i] = DebugPowerParamConvertUtils.getArg(targetMethodContent, parameter);
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
            return  DebugPowerSpringUtils.getBean(parameter.getParameterType());
        } else if (RunContentType.LAMBDA.getType().equals(runContentDTO.getType())) {
            if (parameter.getParameterType().isInterface() && (runContentDTO.getContent().toString().contains("->") || runContentDTO.getContent().toString().contains("::"))) {
                return DebugPowerLambdaUtils.createLambda(runContentDTO.getContent().toString(), parameter.getNestedGenericParameterType());
            }
        } else if (RunContentType.JSON_ENTITY.getType().equals(runContentDTO.getType())) {
            String jsonStr = DebugPowerJsonUtils.getInstance().toJson(runContentDTO.getContent());
            return DebugPowerJsonUtils.getInstance().fromJson(jsonStr, ResolvableType.forMethodParameter(parameter).getType());
        } else if (RunContentType.SIMPLE.getType().equals(runContentDTO.getType())) {
            if (DebugPowerClassUtils.isSimpleValueType(parameter.getParameterType())) {
                // spring简单类型转换
                return simpleTypeConverter.convertIfNecessary(runContentDTO.getContent(), parameter.getParameterType(), parameter);
            }
        } else if (RunContentType.ENUM.getType().equals(runContentDTO.getType())) {
            return Enum.valueOf((Class<? extends Enum>)parameter.getParameterType(), runContentDTO.getContent().toString());
        }
        return null;
    }

    private static RunContentDTO getRunContentDTO(Map<String, RunContentDTO> contentMap, SynthesizingMethodParameter parameter) {
        String parameterName = parameter.getParameterName();
        if (StringUtils.isNotBlank(parameterName)) {
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
