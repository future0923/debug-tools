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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.ExprEditor;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.MethodCall;

/**
 * 对Spring的ConfigurationClassPostProcessor进行转换。
 */
public class ConfigurationClassPostProcessorTransformer {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationClassPostProcessorTransformer.class);

    @OnClassLoadEvent(classNameRegexp = "org.springframework.context.annotation.ConfigurationClassPostProcessor")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        LOGGER.debug("Class 'org.springframework.context.annotation.ConfigurationClassPostProcessor' patched with processor registration.");
        // processConfigBeanDefinitions 方法获取所有的 BeanDefinition
        // 检查这些 BeanDefinition 是否符合 @Configuration 类的特性。
        // 对符合要求的类进行递归解析，生成新的 BeanDefinition，并注册到容器中。
        CtMethod method = clazz.getDeclaredMethod("processConfigBeanDefinitions", new CtClass[]{classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry")});
        method.insertAfter("io.github.future0923.debug.tools.hotswap.core.plugin.spring.core.ConfigurationClassPostProcessorEnhance.getInstance($1).setProcessor(this);");
        try {
            // 对enhanceConfigurationClasses方法内调用的ConfigurableListableBeanFactory#containsSingleton方法进行增强
            // enhanceConfigurationClasses功能如下
            // @Configuration
            //  public class AppConfig {
            //
            //    @Bean
            //    public ServiceA serviceA() {
            //        return new ServiceA();
            //    }
            //
            //    @Bean
            //    public ServiceB serviceB() {
            //        return new ServiceB(serviceA());
            //    }
            //  }
            // 调用 serviceB() 会导致直接调用 serviceA() 方法，可能返回一个新实例（如果没有容器管理）。
            // 增强后
            // 调用 serviceB() 时，serviceA() 会被代理，返回容器中已经管理的单例 ServiceA 实例。
            CtMethod enhanceConfigurationClassesMethod = clazz.getDeclaredMethod("enhanceConfigurationClasses");
            enhanceConfigurationClassesMethod.instrument(new ExprEditor() {
                        @Override
                        public void edit(MethodCall m) throws CannotCompileException {
                            if (m.getClassName().equals("org.springframework.beans.factory.config.ConfigurableListableBeanFactory")
                                    && m.getMethodName().equals("containsSingleton")) {
                                // $proceed($$) 表示执行原方法
                                // $_ 原为返回值
                                // 在进行热重载时阻止正常处理，没热重载时正常处理
                                m.replace("{$_ = $proceed($$) && " +
                                        "(io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.BeanFactoryAssistant.getBeanFactoryAssistant($0) == null || " +
                                        "!io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.BeanFactoryAssistant.getBeanFactoryAssistant($0).isReload());" +
                                        "}");
                            }
                        }
                    });
        } catch (NotFoundException e) {
            // ignore
        }

    }
}
