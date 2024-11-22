/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 可加载可拆除Bean
 */
public class DetachableBeanHolder implements Serializable {

    private static final long serialVersionUID = -7443802320153815102L;

    private final static Logger LOGGER = Logger.getLogger(DetachableBeanHolder.class);

    private Object bean;

    private final Object beanFactory;

    private final Class<?>[] paramClasses;

    private final Object[] paramValues;

    private static final List<WeakReference<DetachableBeanHolder>> beanProxies = Collections.synchronizedList(new ArrayList<>());

    /**
     * @param bean         调用Spring的getBean返回的bean
     * @param beanFactory  Spring beanFactory
     * @param paramClasses {@link ProxyReplacer#FACTORY_METHOD_NAME}的参数Class
     * @param paramValues  {@link ProxyReplacer#FACTORY_METHOD_NAME}的参数值
     */
    public DetachableBeanHolder(Object bean, Object beanFactory, Class<?>[] paramClasses, Object[] paramValues) {
        if (bean == null) {
            LOGGER.error("Bean is null. The param value: {}", Arrays.toString(paramValues));
        }
        this.bean = bean;
        this.beanFactory = beanFactory;
        this.paramClasses = paramClasses;
        this.paramValues = paramValues;
        beanProxies.add(new WeakReference<>(this));
    }

    /**
     * 清除所有代理中的bean引用
     */
    public static void detachBeans() {
        int i = 0;
        synchronized (beanProxies) {
            while (i < beanProxies.size()) {
                DetachableBeanHolder beanHolder = beanProxies.get(i).get();
                if (beanHolder != null) {
                    beanHolder.detach();
                    i++;
                } else {
                    beanProxies.remove(i);
                }
            }
        }
        if (i > 0) {
            LOGGER.debug("{} Spring proxies reset", i);
        } else {
            LOGGER.debug("No spring proxies reset");
        }
    }

    /**
     * 清除这个代理的bean
     */
    public void detach() {
        bean = null;
    }


    /**
     * 设置当前代理的目标Bean
     */
    public void setTarget(Object bean) {
        this.bean = bean;
    }

    /**
     * 获取当前代理的目标Bean
     */
    public Object getTarget() {
        return bean;
    }

    /**
     * 返回现有bean实例或从Spring BeanFactory获取一个新的Bean
     */
    public Object getBean() throws IllegalAccessException, InvocationTargetException {
        Object beanCopy = bean;
        if (beanCopy == null) {
            Method[] methods = beanFactory.getClass().getMethods();
            for (Method factoryMethod : methods) {
                if (ProxyReplacer.FACTORY_METHOD_NAME.equals(factoryMethod.getName())
                        && Arrays.equals(factoryMethod.getParameterTypes(), paramClasses)) {

                    Object freshBean = factoryMethod.invoke(beanFactory, paramValues);

                    // Factory returns HA proxy, but current method is invoked from HA proxy!
                    // It might be the same object (if factory returns same object - meaning
                    // that although clearAllProxies() was called, this bean did not change)
                    // Unwrap the target bean, it is always available
                    // see org.hotswap.agent.plugin.spring.getbean.EnhancerProxyCreater.create()
                    if (freshBean instanceof SpringHotswapAgentProxy) {
                        freshBean = ((SpringHotswapAgentProxy) freshBean).$$ha$getTarget();
                    }
                    bean = freshBean;
                    beanCopy = bean;
                    if (beanCopy == null) {
                        LOGGER.debug("Bean of '{}' not loaded, {} ", bean.getClass().getName(), paramValues);
                        break;
                    }
                    LOGGER.debug("Bean '{}' loaded", bean.getClass().getName());
                    break;
                }
            }
        }
        return beanCopy;
    }

    protected boolean isBeanLoaded() {
        return bean != null;
    }
}