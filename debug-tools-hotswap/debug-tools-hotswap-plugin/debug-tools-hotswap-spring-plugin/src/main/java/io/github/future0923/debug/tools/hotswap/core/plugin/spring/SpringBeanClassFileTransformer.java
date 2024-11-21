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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring;

import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanRefreshCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;

import java.security.ProtectionDomain;
import java.util.Objects;

public class SpringBeanClassFileTransformer implements HaClassFileTransformer {

    private final ClassLoader appClassLoader;
    private final Scheduler scheduler;
    private final String basePackage;

    public SpringBeanClassFileTransformer(ClassLoader appClassLoader, Scheduler scheduler, String basePackage) {
        this.appClassLoader = appClassLoader;
        this.scheduler = scheduler;
        this.basePackage = basePackage;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        if (classBeingRedefined != null) {
            SpringChangesAnalyzer analyzer = new SpringChangesAnalyzer(appClassLoader);
            if (analyzer.isReloadNeeded(classBeingRedefined, classfileBuffer)) {
                scheduler.scheduleCommand(
                        new ClassPathBeanRefreshCommand(
                                classBeingRedefined.getClassLoader(),
                                basePackage,
                                className,
                                classfileBuffer,
                                scheduler
                        )
                );
            }
        }
        return classfileBuffer;
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpringBeanClassFileTransformer that = (SpringBeanClassFileTransformer) o;
        return Objects.equals(appClassLoader, that.appClassLoader) && Objects.equals(basePackage, that.basePackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appClassLoader, basePackage);
    }
}
