package io.github.future0923.debug.power.server.mock.spring;

import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.server.mock.spring.method.SpringParamConvertUtils;
import io.github.future0923.debug.power.server.mock.spring.request.MockHttpServletRequest;
import io.github.future0923.debug.power.server.mock.spring.request.MockHttpServletResponse;
import org.springframework.aop.framework.AopProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author future0923
 */
public class SpringEnvUtil {

    public static void setRequest(RunDTO runDTO) {
        if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
            ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
            RequestContextHolder.setRequestAttributes(requestAttributes);
        } else {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    public static Method findBridgedMethod(Method targetMethod) {
        return BridgeMethodResolver.findBridgedMethod(targetMethod);
    }

    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        return invocationHandler instanceof AopProxy;
    }

    public static Object[] getArgs(Method bridgedMethod, Map<String, RunContentDTO> targetMethodContent) {
        return SpringParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        return new MockHttpServletRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getResponse();
        }
        return new MockHttpServletResponse();
    }
}
