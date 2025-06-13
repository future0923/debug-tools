/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.extension.spring;

import io.github.future0923.debug.tools.base.hutool.core.collection.CollUtil;
import io.github.future0923.debug.tools.base.hutool.core.collection.CollectionUtil;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.dto.RunContentDTO;
import io.github.future0923.debug.tools.extension.spring.method.SpringParamConvertUtils;
import io.github.future0923.debug.tools.server.utils.BeanInstanceUtils;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author future0923
 */
public class SpringEnvUtil {

    private static final Logger logger = Logger.getLogger(SpringEnvUtil.class);

    private static volatile boolean init = false;

    private static List<BeanFactory> beanFactories;

    private static List<ApplicationContext> applicationContexts;

    private static void initSpringContext() {
        if (!init) {
            synchronized (SpringEnvUtil.class) {
                if (!init) {
                    beanFactories = Arrays.asList(BeanInstanceUtils.getInstances(BeanFactory.class));
                    applicationContexts = sort(BeanInstanceUtils.getInstances(ApplicationContext.class));
                    init = true;
                }
            }
        }
    }

    private static List<ApplicationContext> sort(ApplicationContext[] applicationContexts) {
        if (applicationContexts == null || applicationContexts.length == 0) {
            return Collections.emptyList();
        }
        ArrayList<String> contextNames = new ArrayList<>();
        contextNames.add("WebServerApplicationContext");
        contextNames.add("ReactiveWebApplicationContext");
        contextNames.add("WebApplicationContext");
        contextNames.add("ConfigurableApplicationContext");
        List<ApplicationContext> result = Arrays.asList(applicationContexts);
        result.sort(Comparator.comparing(ApplicationContext::getDisplayName, (name1, name2) -> {
            for (String s : contextNames) {
                if (name1.contains(s)) {
                    return -1;
                }
                if (name2.contains(s)) {
                    return 1;
                }
            }
            return 0;
        }));
        for (ApplicationContext applicationContext : result) {
            // 由于没有set进去，会从执行线程获取类加载器
            if (applicationContext instanceof DefaultResourceLoader) {
                ((DefaultResourceLoader) applicationContext).setClassLoader(applicationContext.getClassLoader());
            }
        }
        return result;
    }

    public static <T> T getLastBean(String beanName) {
        return CollUtil.getLast(getBeans(beanName));
    }

    public static <T> List<T> getBeans(String beanName) {
        initSpringContext();
        List<T> beansByApplicationContext = getBeansByApplicationContext(beanName);
        if (!beansByApplicationContext.isEmpty()) {
            return beansByApplicationContext;
        }
        return getBeansByBeanFactory(beanName);
    }

    public static <T> T getLastBean(Class<T> requiredType) {
        return CollectionUtil.getLast(getBeans(requiredType));
    }

    public static <T> List<T> getBeans(Class<T> requiredType) {
        initSpringContext();
        List<T> beansByApplicationContext = getBeansByApplicationContext(requiredType);
        if (!beansByApplicationContext.isEmpty()) {
            return beansByApplicationContext;
        }
        return getBeansByBeanFactory(requiredType);
    }

    public static <T> void registerBean(T bean) {
        registerBean(Introspector.decapitalize(ClassUtils.getShortName(bean.getClass())), bean);
    }

    public static <T> void registerBean(String beanName, T bean) {
        initSpringContext();
        boolean factoryFound = false;
        for (BeanFactory beanFactory : beanFactories) {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) beanFactory;
                factory.autowireBean(bean);
                factory.registerSingleton(beanName, bean);
                factoryFound = true;
                break;
            }
        }
        if (!factoryFound) {
            for (ApplicationContext applicationContext : applicationContexts) {
                if (applicationContext instanceof ConfigurableApplicationContext) {
                    ConfigurableListableBeanFactory factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                    factory.autowireBean(bean);
                    factory.registerSingleton(beanName, bean);
                    break;
                }
            }
        }
    }

    public static void unregisterBean(String beanName) {
        initSpringContext();
        boolean factoryFound = false;
        for (BeanFactory beanFactory : beanFactories) {
            if (beanFactory instanceof DefaultSingletonBeanRegistry) {
                DefaultSingletonBeanRegistry registry = (DefaultSingletonBeanRegistry) beanFactory;
                registry.destroySingleton(beanName);
                factoryFound = true;
                break;
            }
        }
        if (!factoryFound) {
            for (ApplicationContext applicationContext : applicationContexts) {
                if (applicationContext instanceof ConfigurableApplicationContext) {
                    ConfigurableListableBeanFactory factory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
                    if (factory instanceof DefaultSingletonBeanRegistry) {
                        ((DefaultSingletonBeanRegistry) factory).destroySingleton(beanName);
                        break;
                    }
                }
            }
        }
    }

    private static <T> List<T> getBeansByApplicationContext(Class<T> requiredType) {
        List<T> beanList = new LinkedList<>();
        for (ApplicationContext applicationContext : applicationContexts) {
            try {
                beanList.addAll(applicationContext.getBeansOfType(requiredType).values());
            } catch (BeansException e) {
                if (ProjectConstants.DEBUG) {
                    logger.warning("获取bean信息失败", e);
                }
            }
        }
        return beanList;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getBeansByApplicationContext(String beanName) {
        List<T> beanList = new LinkedList<>();
        for (ApplicationContext applicationContext : applicationContexts) {
            try {
                beanList.add((T) applicationContext.getBean(beanName));
            } catch (BeansException e) {
                if (ProjectConstants.DEBUG) {
                    logger.warning("获取bean信息失败", e);
                }
            }
        }
        return beanList;
    }

    private static <T> List<T> getBeansByBeanFactory(Class<T> requiredType) {
        List<T> beanList = new LinkedList<>();
        for (BeanFactory beanFactory : beanFactories) {
            try {
                beanList.add(beanFactory.getBean(requiredType));
            } catch (BeansException e) {
                if (ProjectConstants.DEBUG) {
                    logger.warning("获取bean信息失败", e);
                }
            }
        }
        return beanList;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> getBeansByBeanFactory(String beanName) {
        List<T> beanList = new LinkedList<>();
        for (BeanFactory beanFactory : beanFactories) {
            try {
                beanList.add((T) beanFactory.getBean(beanName));
            } catch (BeansException e) {
                if (ProjectConstants.DEBUG) {
                    logger.warning("获取bean信息失败", e);
                }
            }
        }
        return beanList;
    }
    
    public static Object getSpringConfig(String value) {
        Environment environment = getLastBean(Environment.class);
        Object property = environment.getProperty(value, Object.class);
        if (property == null) {
            // 尝试获取数组
            List<Object> result = new LinkedList<>();
            int i = 0;
            Object object;
            do {
                object = environment.getProperty(value + "[" + i++ + "]", Object.class);
                if (object != null) {
                    result.add(object);
                }
            } while (object != null);
            if (result.isEmpty()) {
                return null;
            } else {
                return result.toArray(new Object[0]);
            }
        } else {
            return property;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getTargetObject(Object candidate) {
        if (candidate == null) {
            return null;
        }
        try {
            if (isAopProxy(candidate) && candidate instanceof Advised) {
                Object target = ((Advised) candidate).getTargetSource().getTarget();
                if (target != null) {
                    return getTargetObject(target);
                }
            }
        } catch (Throwable e) {
            if (ProjectConstants.DEBUG) {
                logger.warning("获取实例信息失败", e);
            }

        }
        return (T) candidate;
    }

    public static Class<?> getTargetClass(Object candidate) {
        if (candidate == null) {
            return null;
        }
        Class<?> result = null;
        if (candidate instanceof TargetClassAware) {
            result = ((TargetClassAware) candidate).getTargetClass();
        }
        if (result == null) {
            result = (isCglibProxy(candidate) ? candidate.getClass().getSuperclass() : candidate.getClass());
        }
        return result;
    }

    private static boolean isCglibProxy(@Nullable Object object) {
        return (object instanceof SpringProxy &&
                object.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR));
    }

    private static boolean isAopProxy(@Nullable Object object) {
        return (object instanceof SpringProxy && (Proxy.isProxyClass(object.getClass()) ||
                object.getClass().getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)));
    }

    public static Method findBridgedMethod(Method targetMethod) {
        return BridgeMethodResolver.findBridgedMethod(targetMethod);
    }

    public static boolean isAopProxy(InvocationHandler invocationHandler) {
        return invocationHandler instanceof AopProxy;
    }

    public static Object[] getArgs(Method bridgedMethod, Map<String, RunContentDTO> targetMethodContent) {
        return SpringParamConvertUtils.getArgs(bridgedMethod, targetMethodContent);
    }

}
