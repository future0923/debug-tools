package io.github.future0923.debug.power.core;

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

    public void run(Class<?> targetClass, Method bridgedMethod, Object instance, Object[] targetMethodArgs) throws Exception {
        if (instance instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(instance);
            if (invocationHandler instanceof AopProxy) {
                try {
                    System.out.println(invocationHandler.invoke(instance, bridgedMethod, targetMethodArgs));
                    return;
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        Object future0923 = bridgedMethod.invoke(instance, targetMethodArgs);
        System.out.println("DebugPower执行结果：" + future0923);
    }

}
