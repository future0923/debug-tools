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