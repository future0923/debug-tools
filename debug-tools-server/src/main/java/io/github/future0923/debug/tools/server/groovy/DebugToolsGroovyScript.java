/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.server.groovy;

import groovy.lang.Script;
import io.github.future0923.debug.tools.server.utils.BeanInstanceUtils;
import io.github.future0923.debug.tools.server.utils.DebugToolsEnvUtils;

import java.lang.reflect.Array;

/**
 * @author future0923
 */
public abstract class DebugToolsGroovyScript extends Script {

    /**
     * 获取JVM中指定Class所有的实例
     *
     * @param targetClass 要获取的class对象
     * @param <T>         具体的类型
     * @return 实例数组
     */
    public <T> T[] gi(Class<T> targetClass) {
        return getInstances(targetClass);
    }

    /**
     * 获取JVM中指定Class所有的实例
     *
     * @param targetClass 要获取的class对象
     * @param <T>         具体的类型
     * @return 实例数组
     */
    public <T> T[] getInstances(Class<T> targetClass) {
        return BeanInstanceUtils.getInstances(targetClass);
    }

    /**
     * 获取Spring容器中指定Bean的所有实例
     *
     * @param beanClass 要获取的class对象
     * @param <T>       具体类型
     * @return 实例数组
     */
    public <T> T[] gb(Class<T> beanClass) throws Exception {
        return getBean(beanClass);
    }

    /**
     * 获取Spring容器中指定Bean的所有实例
     *
     * @param beanClass 要获取的class对象
     * @param <T>       具体类型
     * @return 实例数组
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getBean(Class<T> beanClass) throws Exception {
        return DebugToolsEnvUtils.getBeans(beanClass).toArray((T[]) Array.newInstance(beanClass, 0));
    }

    /**
     * 获取Spring容器中指定Bean的所有实例
     *
     * @param beanName 要获取的bean名称
     * @param <T>      具体类型
     * @return 实例数组
     */
    public <T> T[] gb(String beanName) throws Exception {
        return getBean(beanName);
    }

    /**
     * 获取Spring容器中指定Bean的所有实例
     *
     * @param beanName 要获取的bean名称
     * @param <T>      具体类型
     * @return 实例数组
     */
    @SuppressWarnings("unchecked")
    public <T> T[] getBean(String beanName) throws Exception {
        return (T[]) DebugToolsEnvUtils.getBeans(beanName).toArray(new Object[0]);
    }

    /**
     * 向Spring容器中注入实例，通过Spring的BeanName规则生成BeanName
     *
     * @param bean 要获取的bean
     * @param <T>  具体类型
     */
    public <T> void rb(T bean) throws Exception {
        registerBean(bean);
    }

    /**
     * 向Spring容器中注入实例，通过Spring的BeanName规则生成BeanName
     *
     * @param bean 要获取的bean
     * @param <T>  具体类型
     */
    public <T> void registerBean(T bean) throws Exception {
        DebugToolsEnvUtils.registerBean(bean);
    }

    /**
     * 向Spring容器中注入实例并指定BeanName
     *
     * @param beanName 注入的BeanName
     * @param bean     要获取的bean
     * @param <T>      具体类型
     */
    public <T> void rb(String beanName, T bean) throws Exception {
        registerBean(beanName, bean);
    }

    /**
     * 向Spring容器中注入实例并指定BeanName
     *
     * @param beanName 注入的BeanName
     * @param bean     要获取的bean
     * @param <T>      具体类型
     */
    public <T> void registerBean(String beanName, T bean) throws Exception {
        DebugToolsEnvUtils.registerBean(beanName, bean);
    }

    /**
     * 销毁指定名称的Bean
     *
     * @param beanName 要销毁的Bean名称
     */
    public void urb(String beanName) throws Exception {
        unregisterBean(beanName);
    }

    /**
     * 销毁指定名称的Bean
     *
     * @param beanName 要销毁的Bean名称
     */
    public void unregisterBean(String beanName) throws Exception {
        DebugToolsEnvUtils.unregisterBean(beanName);
    }

    /**
     * 获取spring运行环境
     *
     * @return 运行环境
     */
    public String gActive() throws Exception {
        return getSpringProfilesActive();
    }

    /**
     * 获取spring运行环境
     *
     * @return 运行环境
     */
    public String getSpringProfilesActive() throws Exception {
        return (String) getSpringConfig("spring.profiles.active");
    }

    /**
     * 获取spring配置
     *
     * @param key 配置key
     * @return 具体配置信息
     */
    public Object gsc(String key) throws Exception {
        return getSpringConfig(key);
    }

    /**
     * 获取spring配置
     *
     * @param key 配置key
     * @return 具体配置信息
     */
    public Object getSpringConfig(String key) throws Exception {
        return DebugToolsEnvUtils.getSpringConfig(key);
    }
}
