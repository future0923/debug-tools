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

    private static Class<?> xxlJobEnvUtil;

    static {
        try {
            springEnvUtil = DebugToolsClassUtils.loadDebugToolsClass("io.github.future0923.debug.tools.server.mock.spring.SpringEnvUtil");
            xxlJobEnvUtil = DebugToolsClassUtils.loadDebugToolsClass("io.github.future0923.debug.tools.server.mock.xxljob.XxlJobEnvUtil");
        } catch (ClassNotFoundException ignored) {
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFirstBean(String beanName) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method getFirstBean = springEnvUtil.getMethod("getFirstBean", String.class);
        return (T) getFirstBean.invoke(null, beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeans(String beanName) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method getBeans = springEnvUtil.getMethod("getBeans", String.class);
        return (List<T>) getBeans.invoke(null, beanName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFirstBean(Class<T> requiredType) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method getFirstBean = springEnvUtil.getMethod("getFirstBean", Class.class);
        return (T) getFirstBean.invoke(null, requiredType);
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeans(Class<T> requiredType) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method getBean = springEnvUtil.getMethod("getBeans", Class.class);
        return (List<T>) getBean.invoke(null, requiredType);
    }

    public static <T> void registerBean(T bean) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", Object.class);
        registerBean.invoke(null, bean);
    }

    public static <T> void registerBean(String beanName, T bean) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", String.class, Object.class);
        registerBean.invoke(null, beanName, bean);
    }

    public static void unregisterBean(String beanName) throws Exception {
        DebugToolsClassUtils.loadClass("org.springframework.beans.factory.BeanFactory");
        Method unregisterBean = springEnvUtil.getMethod("unregisterBean", String.class);
        unregisterBean.invoke(null, beanName);
    }

    public static Object getSpringConfig(String value) throws Exception {
        Class.forName("org.springframework.core.env.Environment");
        Method getSpringConfig = springEnvUtil.getMethod("getSpringConfig", String.class);
        return getSpringConfig.invoke(null, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getTargetObject(Object candidate) {
        try {
            Class.forName("org.springframework.aop.SpringProxy");
            Method getTargetObject = springEnvUtil.getMethod("getTargetObject", Object.class);
            return (T) getTargetObject.invoke(null, candidate);
        } catch (Exception ignored) {
            return (T) candidate;
        }
    }

    public static Class<?> getTargetClass(Object candidate) {
        try {
            Class.forName("org.springframework.aop.SpringProxy");
            Method getTargetClass = springEnvUtil.getMethod("getTargetClass", Object.class);
            return (Class<?>) getTargetClass.invoke(null, candidate);
        } catch (Exception ignored) {
            return candidate.getClass();
        }
    }

    public static Method findBridgedMethod(Method targetMethod) {
        try {
            Class.forName("org.springframework.core.BridgeMethodResolver");
            Method findBridgedMethod = springEnvUtil.getMethod("findBridgedMethod", Method.class);
            return (Method) findBridgedMethod.invoke(null, targetMethod);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return targetMethod;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setRequest(RunDTO runDTO) {
        try {
            Class.forName("org.springframework.web.context.request.RequestContextHolder");
            Method setRequest = springEnvUtil.getMethod("setRequest", RunDTO.class);
            setRequest.invoke(null, runDTO);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        try {
            Class.forName("org.springframework.aop.framework.AopProxy");
            Method setRequest = springEnvUtil.getMethod("isAopProxy", InvocationHandler.class);
            return (boolean) setRequest.invoke(null, invocationHandler);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return false;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object[] getArgs(Method bridgedMethod, Map<String, RunContentDTO> targetMethodContent) {
        try {
            Class.forName("org.springframework.core.ResolvableType");
            Method getArgs = springEnvUtil.getMethod("getArgs", Method.class, Map.class);
            return (Object[]) getArgs.invoke(null, bridgedMethod, targetMethodContent);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return DebugToolsParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getRequest() {
        try {
            Class.forName("org.springframework.http.MediaType");
            Method getRequest = springEnvUtil.getMethod("getRequest");
            return getRequest.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getResponse() {
        try {
            Class.forName("org.springframework.http.MediaType");
            Method getRequest = springEnvUtil.getMethod("getResponse");
            return getRequest.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setXxlJobParam(String jobParam) {
        try {
            Class.forName("com.xxl.job.core.context.XxlJobContext");
            Method setXxlJobParam = xxlJobEnvUtil.getMethod("setXxlJobParam", String.class);
            setXxlJobParam.invoke(null, jobParam);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
