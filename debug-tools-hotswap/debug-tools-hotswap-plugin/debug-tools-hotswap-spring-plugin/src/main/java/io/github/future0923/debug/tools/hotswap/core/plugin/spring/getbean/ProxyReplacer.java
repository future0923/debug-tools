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
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.ProxyReplacerTransformer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * 代理替换器。代理getBean方法，在{@link ProxyReplacerTransformer#replaceBeanWithProxy(CtClass)}中被初始化
 */
public class ProxyReplacer {

    private static final Logger LOGGER = Logger.getLogger(ProxyReplacer.class);

    /**
     * Spring InfrastructureProxy class
     */
    private static Class<?> infrastructureProxyClass;

    /**
     * 代理getBean方法
     */
    public static final String FACTORY_METHOD_NAME = "getBean";

    /**
     * 清除所有代理中的bean引用
     */
    public static void clearAllProxies() {
        DetachableBeanHolder.detachBeans();
    }

    /**
     * 创建SpringBean的代理，主要处理原型Bean。在{@link ProxyReplacerTransformer#replaceBeanWithProxy(CtClass)}中字节码增强到SpringBean方法之后
     */
    public static Object register(Object beanFactory, Object bean, Class<?>[] paramClasses, Object[] paramValues) {
        if (bean == null) {
            return bean;
        }
        // 如果不是basePackagePrefixes下的Bean不处理
        String[] basePackagePrefixes;
        try {
            basePackagePrefixes = (String[]) PluginManager.getInstance().getPlugin("io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin",
                    ProxyReplacer.class.getClassLoader()).getClass().getDeclaredField("basePackagePrefixes").get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            LOGGER.error("load io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin failed, classLoader: {}, exception: {}",
                    ProxyReplacer.class.getClassLoader(), e);
            return bean;
        }

        if (basePackagePrefixes != null) {
            boolean hasMatch = false;
            for (String basePackagePrefix : basePackagePrefixes) {
                if (bean.getClass().getName().startsWith(basePackagePrefix)) {
                    hasMatch = true;
                    break;
                }
            }

            // bean from other package
            if (!hasMatch) {
                LOGGER.info("{} not in basePackagePrefix", bean.getClass().getName());
                return bean;
            }
        }

        // 为原型Bean创建代理并处理Aop逻辑

        // 是 JDK 动态代理对象。
        if (bean.getClass().getName().startsWith("com.sun.proxy.$Proxy")) {
            InvocationHandler handler = new HotswapSpringInvocationHandler(bean, beanFactory, paramClasses, paramValues);
            Class<?>[] interfaces = bean.getClass().getInterfaces();
            try {
                if (!Arrays.asList(interfaces).contains(getInfrastructureProxyClass())) {
                    interfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
                    interfaces[interfaces.length - 1] = getInfrastructureProxyClass();
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("error adding org.springframework.core.InfrastructureProxy to proxy class", e);
            }
            return Proxy.newProxyInstance(bean.getClass().getClassLoader(), interfaces, handler);
        } else if (EnhancerProxyCreater.isSupportedCglibProxy(bean)) {
            if (bean.getClass().getName().contains(EnhancerProxyCreater.CGLIB_NAME_PREFIX)) {
                return bean;
            }
            return EnhancerProxyCreater.createProxy(beanFactory, bean, paramClasses, paramValues);
        }
        return bean;
    }

    /**
     * InfrastructureProxy 是一个 标记接口，表明代理对象是框架基础设施的一部分，而不是用户直接定义的业务逻辑。
     * <p>
     * 提供了一个方法 getWrappedObject()，用于返回被代理的原始对象。
     */
    private static Class<?> getInfrastructureProxyClass() throws ClassNotFoundException {
        if (infrastructureProxyClass == null) {
            infrastructureProxyClass = ProxyReplacer.class.getClassLoader().loadClass("org.springframework.core.InfrastructureProxy");
        }
        return infrastructureProxyClass;
    }
}