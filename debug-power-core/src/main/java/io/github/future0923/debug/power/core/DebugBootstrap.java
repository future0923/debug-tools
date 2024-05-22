package io.github.future0923.debug.power.core;

import io.github.future0923.debug.power.common.dto.RunConfigDTO;
import io.github.future0923.debug.power.common.enums.PrintResultType;
import io.github.future0923.debug.power.common.utils.DebugPowerJsonUtils;
import org.springframework.aop.framework.AopProxy;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author future0923
 */
public class DebugBootstrap {

    private static DebugBootstrap debugBootstrap;

    private DebugBootstrap(Instrumentation inst, Map<String, String> configMap) {
    }

    public static synchronized DebugBootstrap getInstance(Instrumentation inst, Map<String, String> configMap) {
        if (debugBootstrap == null) {
            debugBootstrap = new DebugBootstrap(inst, configMap);
        }
        return debugBootstrap;
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

}
