package io.github.future0923.debug.power.server.utils;

import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugPowerEnvUtils {

    private static Class<?> springEnvUtil;

    private static Class<?> xxlJobEnvUtil;

    static {
        try {
            springEnvUtil = DebugPowerClassUtils.loadDebugPowerClass("io.github.future0923.debug.power.server.mock.spring.SpringEnvUtil");
            xxlJobEnvUtil = DebugPowerClassUtils.loadDebugPowerClass("io.github.future0923.debug.power.server.mock.xxljob.XxlJobEnvUtil");
        } catch (ClassNotFoundException ignored) {
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
            return DebugPowerParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
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
