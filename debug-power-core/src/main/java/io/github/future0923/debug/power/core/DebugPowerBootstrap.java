package io.github.future0923.debug.power.core;

import cn.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.power.base.utils.DebugPowerFileUtils;
import io.github.future0923.debug.power.common.dto.RunConfigDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.exception.ArgsParseException;
import io.github.future0923.debug.power.common.utils.DebugPowerClassUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import io.github.future0923.debug.power.common.utils.DebugPowerParamConvertUtils;
import io.github.future0923.debug.power.core.jvm.VmToolsUtils;
import io.github.future0923.debug.power.core.mock.springmvc.MockHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopProxy;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
public class DebugPowerBootstrap {

    private static DebugPowerBootstrap debugBootstrap;

    private DebugPowerBootstrap() {
        System.out.println(1);
        System.out.println(2);
    }

    public static synchronized DebugPowerBootstrap getInstance() {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugPowerBootstrap();
        }
        return debugBootstrap;
    }

    public void call(String agentArgs, Instrumentation inst) throws Exception {
        RunDTO runDTO = parseArgs(agentArgs);
        Class<?> targetClass = DebugPowerClassUtils.loadClass(runDTO.getTargetClassName());
        Method targetMethod;
        try {
            targetMethod = targetClass.getDeclaredMethod(runDTO.getTargetMethodName(), DebugPowerClassUtils.getTypes(runDTO.getTargetMethodParameterTypes()));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new ArgsParseException("未找到目标方法");
        }

        setRequest(runDTO);

        Object instance = VmToolsUtils.getInstance(targetClass, targetMethod);
        // 获取正确的目标方法（非桥接方法）
        Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(targetMethod);
        ReflectUtil.setAccessible(bridgedMethod);
        Object[] targetMethodArgs = DebugPowerParamConvertUtils.getArgs(bridgedMethod, runDTO.getTargetMethodContent());
        run(targetClass, bridgedMethod, instance, targetMethodArgs, runDTO.getRunConfigDTO());
    }

    public static void setRequest(RunDTO runDTO) {
        if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
            ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
            RequestContextHolder.setRequestAttributes(requestAttributes);
        }
    }

    public void run(Class<?> targetClass, Method bridgedMethod, Object instance, Object[] targetMethodArgs, RunConfigDTO configDTO) throws Exception {
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (invocationHandler instanceof AopProxy) {
                try {
                    printResult(invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs), configDTO);
                    return;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        printResult(bridgedMethod.invoke(instance, targetMethodArgs), configDTO);
    }

    private void printResult(Object result, RunConfigDTO configDTO) {
        if (configDTO == null || configDTO.getPrintResultType() == null || PrintResultType.TOSTRING.equals(configDTO.getPrintResultType())) {
            System.out.println("DebugPower执行结果：" + result);
        } else if (PrintResultType.JSON.equals(configDTO.getPrintResultType())) {
            System.out.println("DebugPower执行结果：");
            System.out.println(DebugPowerJsonUtils.toJsonPrettyStr(result));
        } else if (PrintResultType.NO_PRINT.equals(configDTO.getPrintResultType())) {

        }
    }

    private static RunDTO parseArgs(String agentArgs) {
        try {
            if (StringUtils.isEmpty(agentArgs)) {
                ArgsParseException.throwEx("未读取到参数");
            }
            if (agentArgs.startsWith("file://")) {
                String agentJson = URLDecoder.decode(agentArgs.substring(7), StandardCharsets.UTF_8.name());
                File file = new File(agentJson);
                if (!file.exists()) {
                    ArgsParseException.throwEx("文件不存在：" + agentJson);
                }
                agentArgs = DebugPowerFileUtils.getFileAsString(file);
            }
            return DebugPowerJsonUtils.toBean(agentArgs, RunDTO.class);
        } catch (ArgsParseException e) {
            throw e;
        } catch (Exception e) {
            throw new ArgsParseException(e);
        }
    }
}
