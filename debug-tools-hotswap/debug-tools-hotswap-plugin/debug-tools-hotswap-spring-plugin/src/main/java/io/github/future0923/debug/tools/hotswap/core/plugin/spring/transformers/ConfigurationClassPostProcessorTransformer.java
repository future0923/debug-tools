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

public class ConfigurationClassPostProcessorTransformer {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationClassPostProcessorTransformer.class);

    @OnClassLoadEvent(classNameRegexp = "org.springframework.context.annotation.ConfigurationClassPostProcessor")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        LOGGER.debug("Class 'org.springframework.context.annotation.ConfigurationClassPostProcessor' patched with processor registration.");
        CtMethod method = clazz.getDeclaredMethod("processConfigBeanDefinitions",
                new CtClass[]{classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry")});
        method.insertAfter("io.github.future0923.debug.tools.hotswap.core.plugin.spring.core.ConfigurationClassPostProcessorEnhance.getInstance($1)." +
                "setProcessor(this);");
        try {
            /**
             * remove warning log in org.springframework.context.annotation.ConfigurationClassPostProcessor.enhanceConfigurationClasses
             */
            CtMethod enhanceConfigurationClassesMethod = clazz.getDeclaredMethod("enhanceConfigurationClasses");
            enhanceConfigurationClassesMethod.instrument(new ExprEditor() {
                        @Override
                        public void edit(MethodCall m) throws CannotCompileException {
                            if (m.getClassName().equals("org.springframework.beans.factory.config.ConfigurableListableBeanFactory")
                                    && m.getMethodName().equals("containsSingleton")) {
                                m.replace("{$_ = $proceed($$) && " +
                                        "(io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.BeanFactoryAssistant.getBeanFactoryAssistant($0) == null || " +
                                        "!io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.BeanFactoryAssistant.getBeanFactoryAssistant($0).isReload());}");
                            }
                        }
                    });
        } catch (NotFoundException e) {
            // ignore
        }

    }
}
