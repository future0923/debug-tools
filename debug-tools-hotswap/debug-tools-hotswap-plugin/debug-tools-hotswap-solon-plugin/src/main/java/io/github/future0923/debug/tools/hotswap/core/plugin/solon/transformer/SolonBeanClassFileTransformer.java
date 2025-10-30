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
package io.github.future0923.debug.tools.hotswap.core.plugin.solon.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.command.ClassPathBeanRefreshCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.signature.ClassChangesAnalyzer;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * @author future0923
 */
public class SolonBeanClassFileTransformer implements HaClassFileTransformer {

    private static final Logger logger = Logger.getLogger(SolonBeanClassFileTransformer.class);

    private final ClassLoader appClassLoader;
    private final Scheduler scheduler;
    private final String basePackage;

    public SolonBeanClassFileTransformer(ClassLoader appClassLoader, Scheduler scheduler, String basePackage) {
        this.appClassLoader = appClassLoader;
        this.scheduler = scheduler;
        this.basePackage = basePackage;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classBeingRedefined != null) {
            className = className.replace("/", ".");
            if (ClassChangesAnalyzer.isReloadNeeded(classBeingRedefined, classfileBuffer, appClassLoader)) {
                logger.info("watch change class event, start reloading solon bean, class name:{}", className);
                scheduler.scheduleCommand(new ClassPathBeanRefreshCommand(classBeingRedefined.getClassLoader(), basePackage, className, classfileBuffer));
            } else {
                logger.debug("watch change class event, There is no need to reload solon beans, className:{}", className);
            }
        }
        return classfileBuffer;
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return true;
    }
}
