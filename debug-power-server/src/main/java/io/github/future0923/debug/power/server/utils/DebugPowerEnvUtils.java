package io.github.future0923.debug.power.server.utils;

import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugPowerEnvUtils {

    private static Class<?> springWebUtil;

    static {
        try {
            springWebUtil = Class.forName("io.github.future0923.debug.power.server.mock.spring.SpringEnvUtil");
        } catch (ClassNotFoundException ignored) {
        }
    }

    public static Method findBridgedMethod(Method targetMethod) {
        try {
            Class.forName("org.springframework.core.BridgeMethodResolver");
            Method findBridgedMethod = springWebUtil.getMethod("findBridgedMethod", Method.class);
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
            Method setRequest = springWebUtil.getMethod("setRequest", RunDTO.class);
            setRequest.invoke(null, runDTO);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {

        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        try {
            Class.forName("org.springframework.aop.framework.AopProxy");
            Method setRequest = springWebUtil.getMethod("isAopProxy", InvocationHandler.class);
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
            Method getArgs = springWebUtil.getMethod("getArgs", Method.class, Map.class);
            return (Object[]) getArgs.invoke(null, bridgedMethod, targetMethodContent);
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return DebugPowerParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
