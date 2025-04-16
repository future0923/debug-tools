package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.utils.DebugToolsClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugToolsEnvUtils {

    private static Class<?> springEnvUtil;

    private static Class<?> springServletUtil;

    private static Class<?> springReactiveUtil;

    private static Class<?> xxlJobEnvUtil;

    static {
        try {
            springEnvUtil = DebugToolsClassUtils.loadDebugToolsClass("io.github.future0923.debug.tools.server.mock.spring.SpringEnvUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
        try {
            springServletUtil = DebugToolsClassUtils.loadDebugToolsClass("io.github.future0923.debug.tools.server.mock.spring.SpringServletUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
        try {
            springReactiveUtil = DebugToolsClassUtils.loadDebugToolsClass("io.github.future0923.debug.tools.server.mock.spring.SpringReactiveUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
        try {
            xxlJobEnvUtil = DebugToolsClassUtils.loadDebugToolsClass("io.github.future0923.debug.tools.server.mock.xxljob.XxlJobEnvUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFirstBean(String beanName) throws Exception {
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method getFirstBean = springEnvUtil.getMethod("getFirstBean", String.class);
        return (T) getFirstBean.invoke(null, beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeans(String beanName) throws Exception {
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method getBeans = springEnvUtil.getMethod("getBeans", String.class);
        return (List<T>) getBeans.invoke(null, beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFirstBean(Class<T> requiredType) throws Exception {
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method getFirstBean = springEnvUtil.getMethod("getFirstBean", Class.class);
        return (T) getFirstBean.invoke(null, requiredType);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeans(Class<T> requiredType) throws Exception {
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method getBean = springEnvUtil.getMethod("getBeans", Class.class);
        return (List<T>) getBean.invoke(null, requiredType);
    }

    public static <T> void registerBean(T bean) throws Exception {
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", Object.class);
        registerBean.invoke(null, bean);
    }

    public static <T> void registerBean(String beanName, T bean) throws Exception {
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", String.class, Object.class);
        registerBean.invoke(null, beanName, bean);
    }

    public static void unregisterBean(String beanName) throws Exception {
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.beans.factory.BeanFactory");
        Method unregisterBean = springEnvUtil.getMethod("unregisterBean", String.class);
        unregisterBean.invoke(null, beanName);
    }

    public static Object getSpringConfig(String value) throws Exception {
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass("org.springframework.core.env.Environment");
        Method getSpringConfig = springEnvUtil.getMethod("getSpringConfig", String.class);
        return getSpringConfig.invoke(null, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getTargetObject(Object candidate) {
        if (springEnvUtil == null) {
            return (T) candidate;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.aop.SpringProxy");
            Method getTargetObject = springEnvUtil.getMethod("getTargetObject", Object.class);
            return (T) getTargetObject.invoke(null, candidate);
        } catch (Exception ignored) {
            return (T) candidate;
        }
    }

    public static Class<?> getTargetClass(Object candidate) {
        if (springEnvUtil == null) {
            return candidate.getClass();
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.aop.SpringProxy");
            Method getTargetClass = springEnvUtil.getMethod("getTargetClass", Object.class);
            return (Class<?>) getTargetClass.invoke(null, candidate);
        } catch (Exception ignored) {
            return candidate.getClass();
        }
    }

    public static Method findBridgedMethod(Method targetMethod) {
        if (springEnvUtil == null) {
            return targetMethod;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.core.BridgeMethodResolver");
            Method findBridgedMethod = springEnvUtil.getMethod("findBridgedMethod", Method.class);
            return (Method) findBridgedMethod.invoke(null, targetMethod);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return targetMethod;
        }
    }

    public static void setRequest(RunDTO runDTO) {
        if (springServletUtil == null) {
            return;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.web.context.request.RequestContextHolder");
            DebugToolsClassUtils.loadDebugToolsClass("javax.servlet.http.HttpServletRequest");
            Method setRequest = springServletUtil.getMethod("setRequest", RunDTO.class);
            setRequest.invoke(null, runDTO);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
        }
    }

    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        if (springEnvUtil == null) {
            return false;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.aop.framework.AopProxy");
            Method setRequest = springEnvUtil.getMethod("isAopProxy", InvocationHandler.class);
            return (boolean) setRequest.invoke(null, invocationHandler);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return false;
        }
    }

    public static Object[] getArgs(Method bridgedMethod, Map<String, RunContentDTO> targetMethodContent) {
        if (springEnvUtil == null) {
            return DebugToolsParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.core.ResolvableType");
            Method getArgs = springEnvUtil.getMethod("getArgs", Method.class, Map.class);
            return (Object[]) getArgs.invoke(null, bridgedMethod, targetMethodContent);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return DebugToolsParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
        }
    }

    public static Object getRequest() {
        if (springServletUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.http.MediaType");
            DebugToolsClassUtils.loadDebugToolsClass("javax.servlet.http.HttpServletRequest");
            Method getRequest = springServletUtil.getMethod("getRequest");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Object getServerHttpRequest() {
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.http.server.reactive.ServerHttpRequest");
            Method getRequest = springReactiveUtil.getMethod("getServerHttpRequest");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Object getServerWebExchange() {
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.web.server.ServerWebExchange");
            Method getRequest = springReactiveUtil.getMethod("getServerWebExchange");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Object getResponse() {
        if (springServletUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.http.MediaType");
            Method getRequest = springServletUtil.getMethod("getResponse");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Object getServerHttpResponse() {
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("org.springframework.http.server.reactive.ServerHttpResponse");
            Method getRequest = springReactiveUtil.getMethod("getServerHttpResponse");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static void setXxlJobParam(String jobParam) {
        if (xxlJobEnvUtil == null) {
            return;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass("com.xxl.job.core.context.XxlJobContext");
            Method setXxlJobParam = xxlJobEnvUtil.getMethod("setXxlJobParam", String.class);
            setXxlJobParam.invoke(null, jobParam);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
        }
    }
}
