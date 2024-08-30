package io.github.future0923.debug.power.server.groovy;

import groovy.lang.Script;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;

/**
 * @author future0923
 */
public abstract class DebugPowerGroovyScript extends Script {

    public Object[] gb(Class<?> targetClass) {
        return getBean(targetClass);
    }

    public Object[] getBean(Class<?> targetClass) {
        return VmToolsUtils.getInstance(targetClass);
    }
}
