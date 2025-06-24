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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 代理JDK动态代理实现的Bean。cglib的实现为{@link EnhancerProxyCreater}。
 * <p>如果调用的{@code InfrastructureProxy#getWrappedObject}返回原始对象，那么就返回原始对象
 * <p>否则就调用{@link #getBean()}返回对象
 */
public class HotswapSpringInvocationHandler extends DetachableBeanHolder implements InvocationHandler {

    private static final long serialVersionUID = 8037007940960065166L;

    /**
     * @param bean         调用Spring的getBean返回的bean
     * @param beanFactory  Spring beanFactory
     * @param paramClasses {@link ProxyReplacer#FACTORY_METHOD_NAME}的参数Class
     * @param paramValues  {@link ProxyReplacer#FACTORY_METHOD_NAME}的参数Class参数
     */
    public HotswapSpringInvocationHandler(Object bean, Object beanFactory, Class<?>[] paramClasses, Object[] paramValues) {
        super(bean, beanFactory, paramClasses, paramValues);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getWrappedObject")
                // 如果方法声明在 org.springframework.core.InfrastructureProxy 类中
                && method.getDeclaringClass().getName().equals("org.springframework.core.InfrastructureProxy")) {
            for (Class<?> beanInterface : getBean().getClass().getInterfaces()) {
                if (beanInterface.getName().equals("org.springframework.core.InfrastructureProxy")) {
                    return doInvoke(method, args);
                }
            }
            return getBean();
        }
        return doInvoke(method, args);
    }

    /**
     * 执行代理原始方法
     */
    private Object doInvoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(getBean(), args);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}