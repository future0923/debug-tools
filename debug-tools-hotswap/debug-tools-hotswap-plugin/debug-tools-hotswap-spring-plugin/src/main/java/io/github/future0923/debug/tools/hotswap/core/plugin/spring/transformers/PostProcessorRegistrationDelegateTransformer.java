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
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.ExprEditor;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.MethodCall;

public class PostProcessorRegistrationDelegateTransformer {
    private static final Logger LOGGER = Logger.getLogger(PostProcessorRegistrationDelegateTransformer.class);

    /**
     * @param clazz
     * @param classPool
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.context.support.PostProcessorRegistrationDelegate")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        clazz.addField(CtField.make("private static final io.github.future0923.debug.tools.base.logging.Logger LOGGER = " +
                "io.github.future0923.debug.tools.base.logging.Logger.getLogger(org.springframework.context.support.PostProcessorRegistrationDelegate.class);", clazz));

        CtMethod ctMethod = clazz.getDeclaredMethod("invokeBeanFactoryPostProcessors", new CtClass[]{classPool.get("java.util.Collection"),
                classPool.get("org.springframework.beans.factory.config.ConfigurableListableBeanFactory")});
        ctMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("org.springframework.beans.factory.config.BeanFactoryPostProcessor")
                        && m.getMethodName().equals("postProcessBeanFactory")) {
                    m.replace("{  try{ $_ = $proceed($$); " +
                            "}catch (java.lang.Exception e) {\n" +
                            "                LOGGER.debug(\"Failed to invoke BeanDefinitionRegistryPostProcessor: {}, reason:{}\",\n" +
                            "                        new java.lang.Object[]{$0.getClass().getName(), e.getMessage()});\n" +
                            "            };}");
                }
            }
        });
        LOGGER.debug("class 'org.springframework.beans.factory.config.PlaceholderConfigurerSupport' patched with placeholder keep.");
    }
}
