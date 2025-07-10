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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
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
