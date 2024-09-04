package io.github.future0923.debug.power.server.mock.spring;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.common.dto.RunContentDTO;
import io.github.future0923.debug.power.common.dto.RunDTO;
import io.github.future0923.debug.power.server.jvm.VmToolsUtils;
import io.github.future0923.debug.power.server.mock.spring.method.SpringParamConvertUtils;
import io.github.future0923.debug.power.server.mock.spring.request.MockHttpServletRequest;
import io.github.future0923.debug.power.server.mock.spring.request.MockHttpServletResponse;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetClassAware;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                    beanFactories = Arrays.asList(VmToolsUtils.getInstances(BeanFactory.class));
                    applicationContexts = sort(VmToolsUtils.getInstances(ApplicationContext.class));
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
            // 当处于懒加载期间，会有获取到 DebugPowerClassloader 的情况，这里兼容处理
            if (applicationContext instanceof DefaultResourceLoader) {
                ((DefaultResourceLoader) applicationContext).setClassLoader(applicationContext.getClassLoader());
            }
        }
        return result;
    }

    public static <T> T getFirstBean(String beanName) {
        return CollUtil.getFirst(getBeans(beanName));
    }

    public static <T> List<T> getBeans(String beanName) {
        initSpringContext();
        List<T> beansByApplicationContext = getBeansByApplicationContext(beanName);
        if (!beansByApplicationContext.isEmpty()) {
            return beansByApplicationContext;
        }
        return getBeansByBeanFactory(beanName);
    }

    public static <T> T getFirstBean(Class<T> requiredType) {
        return CollectionUtil.getFirst(getBeans(requiredType));
    }

    public static <T> List<T> getBeans(Class<T> requiredType) {
        initSpringContext();
        List<T> beansByApplicationContext = getBeansByApplicationContext(requiredType);
        if (!beansByApplicationContext.isEmpty()) {
            return beansByApplicationContext;
        }
        return getBeansByBeanFactory(requiredType);
    }

    private static <T> List<T> getBeansByApplicationContext(Class<T> requiredType) {
        List<T> beanList = new LinkedList<>();
        for (ApplicationContext applicationContext : applicationContexts) {
            try {
                beanList.addAll(applicationContext.getBeansOfType(requiredType).values());
            } catch (BeansException ignored) {
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
            } catch (BeansException ignored) {
            }
        }
        return beanList;
    }

    private static <T> List<T> getBeansByBeanFactory(Class<T> requiredType) {
        List<T> beanList = new LinkedList<>();
        for (BeanFactory beanFactory : beanFactories) {
            try {
                beanList.add(beanFactory.getBean(requiredType));
            } catch (BeansException ignored) {
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
            } catch (BeansException ignored) {
            }
        }
        return beanList;
    }
    
    public static Object getSpringConfig(String value) {
        Environment environment = getFirstBean(Environment.class);
        return environment.getProperty(value, Object.class);
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
        } catch (Throwable ignored) {

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

    public static void setRequest(RunDTO runDTO) {
        if (runDTO.getHeaders() != null && !runDTO.getHeaders().isEmpty()) {
            MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
            runDTO.getHeaders().forEach(mockHttpServletRequest::addHeader);
            ServletRequestAttributes requestAttributes = new ServletRequestAttributes(mockHttpServletRequest);
            RequestContextHolder.setRequestAttributes(requestAttributes);
        } else {
            RequestContextHolder.resetRequestAttributes();
        }
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

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getRequest();
        }
        return new MockHttpServletRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            return requestAttributes.getResponse();
        }
        return new MockHttpServletResponse();
    }


}
