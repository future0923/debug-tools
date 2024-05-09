package io.github.future0923.debug.power.core;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
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
        Thread.currentThread().setContextClassLoader(DebugBootstrap.class.getClassLoader());
        Object future0923 = bridgedMethod.invoke(instance, targetMethodArgs);
        System.out.println(future0923);
    }

}
