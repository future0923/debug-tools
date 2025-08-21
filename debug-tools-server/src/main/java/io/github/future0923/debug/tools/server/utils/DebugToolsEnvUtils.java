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

    @SuppressWarnings("unchecked")
    private static  <T> T getSpringLastBean(Class<T> requiredType) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method getLastBean = springEnvUtil.getMethod("getLastBean", Class.class);
        return (T) getLastBean.invoke(null, requiredType);
    }

    @SuppressWarnings("unchecked")
    private static  <T> T getSolonLastBean(Class<T> requiredType) throws Exception {
        Class<?> solonEnvUtil = getSolonEnvUtilClass();
        if (solonEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.noear.solon.core.AppContext");
        Method getLastBean = solonEnvUtil.getMethod("getLastBean", Class.class);
        return (T) getLastBean.invoke(null, requiredType);
    }

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

    public static <T> void registerBean(T bean) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", Object.class);
        registerBean.invoke(null, bean);
    }

    public static <T> void registerBean(String beanName, T bean) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method registerBean = springEnvUtil.getMethod("registerBean", String.class, Object.class);
        registerBean.invoke(null, beanName, bean);
    }

    public static void unregisterBean(String beanName) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.beans.factory.BeanFactory");
        Method unregisterBean = springEnvUtil.getMethod("unregisterBean", String.class);
        unregisterBean.invoke(null, beanName);
    }

    public static Object getSpringConfig(String value) throws Exception {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return null;
        }
        DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.core.env.Environment");
        Method getSpringConfig = springEnvUtil.getMethod("getSpringConfig", String.class);
        return getSpringConfig.invoke(null, value);
    }

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

    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        Class<?> springEnvUtil = getSpringEnvUtilClass();
        if (springEnvUtil == null) {
            return false;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.aop.framework.AopProxy");
            Method setRequest = springEnvUtil.getMethod("isAopProxy", InvocationHandler.class);
            return (boolean) setRequest.invoke(null, invocationHandler);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return false;
        }
    }

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

    public static Object getResponse() {
        Class<?> springServletUtil = getSpringServletUtil();
        if (springServletUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.http.MediaType");
            Method getRequest = springServletUtil.getMethod("getResponse");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Object getServerHttpResponse() {
        Class<?> springReactiveUtil = getSpringReactiveUtil();
        if (springReactiveUtil == null) {
            return null;
        }
        try {
            DebugToolsClassUtils.loadDebugToolsClass(appClassLoader, "org.springframework.http.server.reactive.ServerHttpResponse");
            Method getRequest = springReactiveUtil.getMethod("getServerHttpResponse");
            return getRequest.invoke(null);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception ignored) {
            return null;
        }
    }

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
