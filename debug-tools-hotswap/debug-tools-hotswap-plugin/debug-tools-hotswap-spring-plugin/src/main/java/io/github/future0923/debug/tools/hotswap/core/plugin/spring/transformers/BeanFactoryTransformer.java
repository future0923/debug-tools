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
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.core.BeanFactoryProcessor;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.support.InitMethodEnhance;

/**
 * Spring BeanFactory Transformer
 */
public class BeanFactoryTransformer {

    private static final Logger LOGGER = Logger.getLogger(BeanFactoryTransformer.class);

    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.DefaultSingletonBeanRegistry")
    public static void registerDefaultSingletonBeanRegistry(ClassLoader appClassLoader, CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        clazz.addField(CtField.make("private java.util.Set hotswapAgent$destroyBean = new java.util.HashSet();", clazz));
        clazz.addInterface(classPool.get("io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.api.BeanFactoryLifecycle"));
        clazz.addMethod(CtNewMethod.make(
                "public boolean hotswapAgent$isDestroyedBean(String beanName) { " +
                        "return hotswapAgent$destroyBean.contains(beanName); " +
                    "}",
                clazz));
        clazz.addMethod(CtNewMethod.make(
                "public void hotswapAgent$destroyBean(String beanName) { " +
                        "hotswapAgent$destroyBean.add(beanName); " +
                    "}",
                clazz));
        clazz.addMethod(CtNewMethod.make(
                "public void hotswapAgent$clearDestroyBean() { " +
                        "hotswapAgent$destroyBean.clear(); " +
                    "}",
                clazz));
        CtMethod destroySingletonMethod = clazz.getDeclaredMethod("destroySingleton", new CtClass[]{classPool.get(String.class.getName())});
        destroySingletonMethod.insertAfter(BeanFactoryProcessor.class.getName() + ".postProcessDestroySingleton($0, $1);");
    }

    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory")
    public static void registerAbstractAutowireCapableBeanFactory(ClassLoader appClassLoader, CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        clazz.addField(CtField.make("private static final io.github.future0923.debug.tools.base.logging.Logger $$ha$LOGGER = io.github.future0923.debug.tools.base.logging.Logger.getLogger(org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.class);", clazz));
        CtMethod createBeanMethod = clazz.getDeclaredMethod(
                "createBean",
                new CtClass[]{
                    classPool.get(String.class.getName()),
                    classPool.get("org.springframework.beans.factory.support.RootBeanDefinition"),
                    classPool.get(Object[].class.getName())
                }
        );
        createBeanMethod.insertAfter(BeanFactoryProcessor.class.getName() + ".postProcessCreateBean($0, $1, $2);");

        // try catch for custom init method
        CtMethod[] invokeCustomInitMethods = clazz.getDeclaredMethods("invokeCustomInitMethod");
        if (invokeCustomInitMethods.length != 1) {
            LOGGER.error("Unexpected number of 'invokeCustomInitMethod' methods found. Expected: 1, Found: " + invokeCustomInitMethods.length);
        }
        invokeCustomInitMethods[0].addCatch(
                InitMethodEnhance.catchException("$2", "$$ha$LOGGER", "$e", "invokeCustomInitMethod", false),
                classPool.get("java.lang.Throwable"));

        // try catch for afterPropertiesSet
        CtMethod invokeInitMethod = clazz.getDeclaredMethod("invokeInitMethods",
                new CtClass[]{classPool.get(String.class.getName()), classPool.get("java.lang.Object"),
                        classPool.get("org.springframework.beans.factory.support.RootBeanDefinition")});
        invokeInitMethod.addCatch(
                InitMethodEnhance.catchException("$2", "$$ha$LOGGER", "$e", "invokeInitMethods", false),
                classPool.get("java.lang.Throwable"));
    }
}
