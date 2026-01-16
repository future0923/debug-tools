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
package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.base.classloader.DebugToolsExtensionClassLoader;
import io.github.future0923.debug.tools.base.config.AgentConfig;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsClassUtils;
import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.server.http.handler.AllClassLoaderHttpHandler;
import org.springframework.beans.factory.config.BeanDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author future0923
 */
public class DebugToolsEnvUtils {

    private static final Logger logger = Logger.getLogger(DebugToolsEnvUtils.class);

    private static final Map<ClassLoader, DebugToolsExtensionClassLoader> EXTENSION_CLASS_LOADER_MAP = new HashMap<>();

    private static ClassLoader appClassLoader;

    private static DebugToolsExtensionClassLoader getExtensionClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = AllClassLoaderHttpHandler.getDefaultClassLoader();
        }
        appClassLoader = classLoader;
        DebugToolsExtensionClassLoader extensionClassLoader = EXTENSION_CLASS_LOADER_MAP.get(classLoader);
        if (extensionClassLoader != null) {
            return extensionClassLoader;
        }
        synchronized (DebugToolsEnvUtils.class) {
            extensionClassLoader = EXTENSION_CLASS_LOADER_MAP.get(classLoader);
            if (extensionClassLoader != null) {
                return extensionClassLoader;
            }
            List<URL> urls = new LinkedList<>();
            Optional.ofNullable(AgentConfig.INSTANCE.getSpringExtensionURL()).ifPresent(urls::add);
            Optional.ofNullable(AgentConfig.INSTANCE.getSolonExtensionURL()).ifPresent(urls::add);
            Optional.ofNullable(AgentConfig.INSTANCE.getXxlJobExtensionURL()).ifPresent(urls::add);
            extensionClassLoader = new DebugToolsExtensionClassLoader(urls.toArray(new URL[0]), classLoader);
            EXTENSION_CLASS_LOADER_MAP.put(classLoader, extensionClassLoader);
            return extensionClassLoader;
        }
    }

    public static Class<?> getSpringEnvUtilClass() {
        try {
            return getExtensionClassLoader(Thread.currentThread().getContextClassLoader()).loadClass("io.github.future0923.debug.tools.extension.spring.SpringEnvUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (ProjectConstants.DEBUG) {
                logger.warning("SpringEnvUtil get error", e);
            }
            return null;
        }
    }

    public static Class<?> getSpringServletUtil() {
        try {
            return getExtensionClassLoader(Thread.currentThread().getContextClassLoader()).loadClass("io.github.future0923.debug.tools.extension.spring.SpringServletUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (ProjectConstants.DEBUG) {
                logger.warning("SpringServletUtil get error", e);
            }
            return null;
        }
    }

    public static Class<?> getSpringReactiveUtil() {
        try {
            return getExtensionClassLoader(Thread.currentThread().getContextClassLoader()).loadClass("io.github.future0923.debug.tools.extension.spring.SpringReactiveUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (ProjectConstants.DEBUG) {
                logger.warning("SpringReactiveUtil get error", e);
            }
            return null;
        }
    }

    public static Class<?> getSolonEnvUtilClass() {
        try {
            return getExtensionClassLoader(Thread.currentThread().getContextClassLoader()).loadClass("io.github.future0923.debug.tools.extension.solon.SolonEnvUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (ProjectConstants.DEBUG) {
                logger.warning("SolonEnvUtil get error", e);
            }
            return null;
        }
    }

    public static Class<?> getXxlJobEnvUtil() {
        try {
            return getExtensionClassLoader(Thread.currentThread().getContextClassLoader()).loadClass("io.github.future0923.debug.tools.extension.xxljob.XxlJobEnvUtil");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            if (ProjectConstants.DEBUG) {
                logger.warning("XxlJobEnvUtil get error", e);
            }
            return null;
        }
    }

    /**
     * 根据Bean名称获取最后一个Bean实例
     *
     * @param beanName Bean名称
     * @param <T> Bean类型
     * @return Bean实例，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getLastBean(String beanName) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getLastBean = springEnvUtil.getMethod("getLastBean", String.class);
        return (T) getLastBean.invoke(null, beanName);
    }


    /**
     * 根据Bean名称获取Bean实例列表
     *
     * @param beanName Bean名称
     * @param <T> Bean类型
     * @return Bean实例列表，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeans(String beanName) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getBeans = springEnvUtil.getMethod("getBeans", String.class);
        return (List<T>) getBeans.invoke(null, beanName);
    }

    /**
     * 根据类型获取Bean名称数组
     *
     * @param type Bean类型
     * @return Bean名称数组，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    public static String[] getBeanNamesForType(Class<?> type) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getBeanNamesForType = springEnvUtil.getMethod("getBeanNamesForType", Class.class);
        return (String[]) getBeanNamesForType.invoke(null, type);
    }

    /**
     * 根据Bean名称获取Bean定义
     *
     * @param beanName Bean名称
     * @return Bean定义对象，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    public static BeanDefinition getBeanDefinition(String beanName) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getBeanDefinition = springEnvUtil.getMethod("getBeanDefinition", String.class);
        return (BeanDefinition) getBeanDefinition.invoke(null, beanName);
    }

    /**
     * 根据类型获取Bean实例
     *
     * @param requiredType Bean类型
     * @return Bean实例，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    public static <T> T getLastBean(Class<T> requiredType) throws Exception {
        try {
            T springLastBean = getSpringLastBean(requiredType);
            if (springLastBean != null) {
                return springLastBean;
            }
        } catch (Exception ignore) {
        }
        return getSolonLastBean(requiredType);
    }

    /**
     * 根据类型获取Spring容器中的最后一个Bean实例
     *
     * @param requiredType Bean类型
     * @param <T> Bean类型
     * @return Bean实例，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    @SuppressWarnings("unchecked")
    private static <T> T getSpringLastBean(Class<T> requiredType) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getLastBean = springEnvUtil.getMethod("getLastBean", Class.class);
        return (T) getLastBean.invoke(null, requiredType);
    }

    /**
     * 根据类型获取Solon容器中的最后一个Bean实例
     *
     * @param requiredType Bean类型
     * @param <T> Bean类型
     * @return Bean实例，如果Solon环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    @SuppressWarnings("unchecked")
    private static <T> T getSolonLastBean(Class<T> requiredType) throws Exception {
        Class<?> solonEnvUtil = getSolonEnvUtilClass();
        if (solonEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.noear.solon.core.AppContext");
        Method getLastBean = solonEnvUtil.getMethod("getLastBean", Class.class);
        return (T) getLastBean.invoke(null, requiredType);
    }

    /**
     * 根据类型获取Bean实例列表
     *
     * @param requiredType Bean类型
     * @param <T> Bean类型
     * @return Bean实例列表，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getBeans(Class<T> requiredType) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getBean = springEnvUtil.getMethod("getBeans", Class.class);
        return (List<T>) getBean.invoke(null, requiredType);
    }

    /**
     * 注册Bean到Spring容器
     *
     * @param bean Bean实例
     * @throws Exception 反射调用异常
     */
    public static <T> void registerBean(T bean) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", Object.class);
        registerBean.invoke(null, bean);
    }

    /**
     * 注册Bean到Spring容器
     *
     * @param beanName Bean名称
     * @param bean Bean实例
     * @throws Exception 反射调用异常
     */
    public static <T> void registerBean(String beanName, T bean) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", String.class, Object.class);
        registerBean.invoke(null, beanName, bean);
    }

    /**
     * 注册Bean到Spring容器
     *
     * @param beanName Bean名称
     * @param beanClass Bean类
     * @throws Exception 反射调用异常
     */
    public static <T> void registerBean(String beanName,Class<T> beanClass) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", String.class, Class.class);
        registerBean.invoke(null, beanName, beanClass);
    }

    /**
     * 从Spring容器中注销指定名称的Bean
     *
     * @param beanName 要注销的Bean名称
     * @throws Exception 反射调用异常
     */
    public static void unregisterBean(String beanName) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method unregisterBean = springEnvUtil.getMethod("unregisterBean", String.class);
        unregisterBean.invoke(null, beanName);
    }

    /**
     * 从Spring容器中注销指定名称的Bean以及Bean的定义
     *
     * @param beanName 要注销的Bean名称
     * @throws Exception 反射调用异常
     */
    public static void unregisterBeanAndDefinition(String beanName) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method unregisterBeanAndDefinition = springEnvUtil.getMethod("unregisterBeanAndDefinition", String.class);
        unregisterBeanAndDefinition.invoke(null, beanName);
    }

    /**
     * 获取Spring配置属性的值
     *
     * @param value 配置键
     * @return 配置值，如果Spring环境工具类不存在则返回null
     * @throws Exception 反射调用异常
     */
    public static Object getSpringConfig(String value) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.core.env.Environment");
        Method getSpringConfig = springEnvUtil.getMethod("getSpringConfig", String.class);
        return getSpringConfig.invoke(null, value);
    }

    /**
     * 获取AOP代理对象的原始目标对象
     *
     * @param candidate 可能是代理对象
     * @param <T> 目标对象类型
     * @return 原始目标对象，如果不是代理对象则返回自身
     */
    @SuppressWarnings("unchecked")
    public static <T> T getTargetObject(Object candidate) {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return (T) candidate;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.aop.SpringProxy");
            Method getTargetObject = springEnvUtil.getMethod("getTargetObject", Object.class);
            return (T) getTargetObject.invoke(null, candidate);
        } catch (Exception ignored) {
            return (T) candidate;
        }
    }

    /**
     * 获取AOP代理对象的原始目标类
     *
     * @param candidate 可能是代理对象
     * @return 原始目标类，如果不是代理对象则返回自身的类
     */
    public static Class<?> getTargetClass(Object candidate) {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return candidate.getClass();
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.aop.SpringProxy");
            Method getTargetClass = springEnvUtil.getMethod("getTargetClass", Object.class);
            return (Class<?>) getTargetClass.invoke(null, candidate);
        } catch (Exception ignored) {
            return candidate.getClass();
        }
    }

    /**
     * 查找桥接方法对应的原始方法
     *
     * @param targetMethod 可能是桥接方法
     * @return 原始方法，如果不是桥接方法则返回自身
     */
    public static Method findBridgedMethod(Method targetMethod) {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return targetMethod;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.core.BridgeMethodResolver");
            Method findBridgedMethod = springEnvUtil.getMethod("findBridgedMethod", Method.class);
            return (Method) findBridgedMethod.invoke(null, targetMethod);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return targetMethod;
        }
    }

    /**
     * 设置当前线程的HTTP请求对象
     *
     * @param runDTO 包含请求信息的DTO对象
     */
    public static void setRequest(RunDTO runDTO) {
        Class<?> springServletUtil = getSpringServletUtil();
        if (springServletUtil == null) {
            return;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.web.context.request.RequestContextHolder");
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "javax.servlet.http.HttpServletRequest");
            Method setRequest = springServletUtil.getMethod("setRequest", RunDTO.class);
            setRequest.invoke(null, runDTO);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
        }
    }

    /**
     * 判断调用处理器是否为Spring AOP代理
     *
     * @param invocationHandler 调用处理器
     * @return 如果是Spring AOP代理返回true，否则返回false
     */
    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return false;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.aop.framework.AopProxy");
            Method isAopProxy = springEnvUtil.getMethod("isAopProxy", InvocationHandler.class);
            return (boolean) isAopProxy.invoke(null, invocationHandler);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 根据方法参数信息和运行内容生成参数数组
     *
     * @param bridgedMethod 桥接方法或原始方法
     * @param targetMethodContent 方法参数运行内容映射
     * @return 参数数组
     */
    public static Object[] getArgs(Method bridgedMethod, Map<String, RunContentDTO> targetMethodContent) {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return DebugToolsParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.core.ResolvableType");
            Method getArgs = springEnvUtil.getMethod("getArgs", Method.class, Map.class);
            return (Object[]) getArgs.invoke(null, bridgedMethod, targetMethodContent);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return DebugToolsParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
        }
    }

    /**
     * 获取当前线程的HTTP请求对象
     *
     * @return HTTP请求对象，如果Spring Servlet工具类不存在则返回null
     */
    public static Object getRequest() {
        Class<?> springServletUtil = getSpringServletUtil();
        if (springServletUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.http.MediaType");
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "javax.servlet.http.HttpServletRequest");
            Method getRequest = springServletUtil.getMethod("getRequest");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 获取当前线程的响应式HTTP请求对象
     *
     * @return ServerHttpRequest对象，如果Spring Reactive工具类不存在则返回null
     */
    public static Object getServerHttpRequest() {
        Class<?> springReactiveUtil = getSpringReactiveUtil();
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.http.server.reactive.ServerHttpRequest");
            Method getRequest = springReactiveUtil.getMethod("getServerHttpRequest");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 获取当前线程的ServerWebExchange对象
     *
     * @return ServerWebExchange对象，如果Spring Reactive工具类不存在则返回null
     */
    public static Object getServerWebExchange() {
        Class<?> springReactiveUtil = getSpringReactiveUtil();
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.web.server.ServerWebExchange");
            Method getRequest = springReactiveUtil.getMethod("getServerWebExchange");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 获取当前线程的HTTP响应对象
     *
     * @return HTTP响应对象，如果Spring Servlet工具类不存在则返回null
     */
    public static Object getResponse() {
        Class<?> springServletUtil = getSpringServletUtil();
        if (springServletUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.http.MediaType");
            Method getResponse = springServletUtil.getMethod("getResponse");
            return getResponse.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 获取当前线程的响应式HTTP响应对象
     *
     * @return ServerHttpResponse对象，如果Spring Reactive工具类不存在则返回null
     */
    public static Object getServerHttpResponse() {
        Class<?> springReactiveUtil = getSpringReactiveUtil();
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.http.server.reactive.ServerHttpResponse");
            Method getServerHttpResponse = springReactiveUtil.getMethod("getServerHttpResponse");
            return getServerHttpResponse.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * 设置XXL-Job任务参数
     *
     * @param jobParam 任务参数
     */
    public static void setXxlJobParam(String jobParam) {
        Class<?> xxlJobEnvUtil = getXxlJobEnvUtil();
        if (xxlJobEnvUtil == null) {
            return;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "com.xxl.job.core.context.XxlJobContext");
            Method setXxlJobParam = xxlJobEnvUtil.getMethod("setXxlJobParam", String.class);
            setXxlJobParam.invoke(null, jobParam);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
        }
    }
}
