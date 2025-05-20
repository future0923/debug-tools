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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

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
public class ClassPathBeanDefinitionScannerPatcher {

    private static final Logger LOGGER = Logger.getLogger(ClassPathBeanDefinitionScannerPatcher.class);

    /**
     * 没配置SpringBasePackagePrefixes的话就识别Spring扫描的路径注册
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

            LOGGER.debug("Class 'org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider' patched with basePackage registration.");
        } else {
            LOGGER.debug("No need to register scanned path, instead just register 'spring.basePackagePrefix' in configuration file.");
        }
    }
}
