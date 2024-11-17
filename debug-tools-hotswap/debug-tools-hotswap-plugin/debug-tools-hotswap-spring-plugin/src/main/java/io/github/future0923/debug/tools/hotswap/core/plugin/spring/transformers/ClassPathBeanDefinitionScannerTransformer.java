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
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.SpringPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

/**
 * 如果{@link SpringPlugin#basePackagePrefixes}没有配置，那么就解析Spring的{@link ClassPathScanningCandidateComponentProvider#findCandidateComponents}方法获取到Spring扫描的路径通过{@link ClassPathBeanDefinitionScannerAgent#registerBasePackage}注入
 */
public class ClassPathBeanDefinitionScannerTransformer {
    private static final Logger LOGGER = Logger.getLogger(ClassPathBeanDefinitionScannerTransformer.class);

    /**
     * Insert at the beginning of the method:
     * <pre>public Set<BeanDefinition> findCandidateComponents(String basePackage)</pre>
     * new code to initialize ClassPathBeanDefinitionScannerAgent for a base class
     * It would be better to override a more appropriate method
     * org.springframework.context.annotation.ClassPathBeanDefinitionScanner.scan() directly,
     * however there are issues with javassist and varargs parameters.
     * <pre>
     *  if (this instanceof org.springframework.context.annotation.ClassPathBeanDefinitionScanner) {
     *      if (io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent.getInstance($1) == null) {
     *          io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent.getInstance((org.springframework.context.annotation.ClassPathBeanDefinitionScanner) this).registerBasePackage($1);
     *      }
     *  }
     * </pre>
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        if (SpringPlugin.basePackagePrefixes == null) {
            CtMethod method = clazz.getDeclaredMethod("findCandidateComponents", new CtClass[]{classPool.get("java.lang.String")});
            method.insertAfter(
                    "if (this instanceof org.springframework.context.annotation.ClassPathBeanDefinitionScanner) {" +
                            "  if (io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent.getInstance($1) == null) {" +
                            "    io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent.getInstance(" +
                            "(org.springframework.context.annotation.ClassPathBeanDefinitionScanner)this).registerBasePackage($1);" +
                            "  }" +
                            "}");

            LOGGER.debug("Class 'org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider' " +
                    "patched with basePackage registration.");
        } else {
            LOGGER.debug("No need to register scanned path, instead just register 'spring.basePackagePrefix' in " +
                    "configuration file.");
        }
    }
}
