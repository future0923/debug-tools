package io.github.future0923.debug.power.server.groovy;

import groovy.lang.Script;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;
import io.github.future0923.debug.power.server.utils.DebugPowerEnvUtils;

import java.lang.reflect.Array;

/**
 * @author future0923
 */
public abstract class DebugPowerGroovyScript extends Script {

    public <T> T[] gi(Class<T> targetClass) {
        return getInstances(targetClass);
    }

    public  <T> T[] getInstances(Class<T> targetClass) {
        return VmToolsUtils.getInstances(targetClass);
    }

    public <T> T[] gb(Class<T> beanClass) {
        return getBean(beanClass);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getBean(Class<T> beanClass) {
        return DebugPowerEnvUtils.getBeans(beanClass).toArray((T[])Array.newInstance(beanClass, 0));
    }

    public <T> T[] gb(String beanName) {
        return getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getBean(String beanName) {
        return (T[]) DebugPowerEnvUtils.getBeans(beanName).toArray(new Object[0]);
    }

    public String gActive() {
        return getSpringProfilesActive();
    }

    public String getSpringProfilesActive() {
        // TODO 获取spring环境
        return null;
    }

    public Object gsv(String value) {
        return getSpringValue(value);
    }

    public Object getSpringValue(String value) {
        return DebugPowerEnvUtils.getSpringValue(value);
    }
}
