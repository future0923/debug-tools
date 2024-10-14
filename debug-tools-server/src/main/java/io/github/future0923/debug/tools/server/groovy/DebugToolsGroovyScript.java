package io.github.future0923.debug.tools.server.groovy;

import groovy.lang.Script;
import io.github.future0923.debug.tools.server.jvm.VmToolsUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;

import java.lang.reflect.Array;

/**
 * @author future0923
 */
public abstract class DebugToolsGroovyScript extends Script {

    public <T> T[] gi(Class<T> targetClass) {
        return getInstances(targetClass);
    }

    public  <T> T[] getInstances(Class<T> targetClass) {
        return VmToolsUtils.getInstances(targetClass);
    }

    public <T> T[] gb(Class<T> beanClass) throws Exception {
        return getBean(beanClass);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getBean(Class<T> beanClass) throws Exception {
        return DebugToolsEnvUtils.getBeans(beanClass).toArray((T[])Array.newInstance(beanClass, 0));
    }

    public <T> T[] gb(String beanName) throws Exception {
        return getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public <T> T[] getBean(String beanName) throws Exception {
        return (T[]) DebugToolsEnvUtils.getBeans(beanName).toArray(new Object[0]);
    }

    public <T> void rb(T bean) throws Exception {
        registerBean(bean);
    }

    public <T> void registerBean(T bean) throws Exception {
        DebugToolsEnvUtils.registerBean(bean);
    }

    public <T> void rb(String beanName, T bean) throws Exception {
        registerBean(beanName, bean);
    }
    public <T> void registerBean(String beanName, T bean) throws Exception {
        DebugToolsEnvUtils.registerBean(beanName, bean);
    }

    public void urb(String beanName) throws Exception {
        unregisterBean(beanName);
    }

    public void unregisterBean(String beanName) throws Exception {
        DebugToolsEnvUtils.unregisterBean(beanName);
    }

    public String gActive() throws Exception {
        return getSpringProfilesActive();
    }

    public String getSpringProfilesActive() throws Exception {
        return (String) getSpringConfig("spring.profiles.active");
    }

    public Object gsc(String value) throws Exception {
        return getSpringConfig(value);
    }

    public Object getSpringConfig(String value) throws Exception {
        return DebugToolsEnvUtils.getSpringConfig(value);
    }
}
