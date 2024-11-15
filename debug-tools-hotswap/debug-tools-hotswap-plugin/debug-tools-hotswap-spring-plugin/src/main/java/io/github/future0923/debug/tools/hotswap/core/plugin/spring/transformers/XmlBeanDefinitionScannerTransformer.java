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
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.files.XmlBeanDefinitionScannerAgent;

/**
 * Hook into classpath scanner process to register basicPackage of scanned classes.
 * <p>
 * Catch changes on component-scan configuration such as (see tests):
 * <pre>&lt;context:component-scan base-package="org.hotswap.agent.plugin.spring.testBeans"/&gt;</pre>
 */
public class XmlBeanDefinitionScannerTransformer {
    private static final Logger LOGGER = Logger.getLogger(XmlBeanDefinitionScannerTransformer.class);

    /**
     * Insert at the beginning of the method:
     * <pre>public Set<BeanDefinition> findCandidateComponents(String basePackage)</pre>
     * new code to initialize ClassPathBeanDefinitionScannerAgent for a base class
     * It would be better to override a more appropriate method
     * org.springframework.context.annotation.ClassPathBeanDefinitionScanner.scan() directly,
     * however there are issues with javassist and varargs parameters.
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.xml.XmlBeanDefinitionReader")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {

        CtMethod method = clazz.getDeclaredMethod("registerBeanDefinitions", new CtClass[]{
                classPool.get("org.w3c.dom.Document"),
                classPool.get("org.springframework.core.io.Resource")});
        method.insertBefore(XmlBeanDefinitionScannerAgent.class.getName() + ".registerXmlBeanDefinitionScannerAgent(this, $2);");
        LOGGER.debug("Class 'org.springframework.beans.factory.xml.XmlBeanDefinitionReader' patched with xmlReader registration.");
    }
}
