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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.core;

import io.github.future0923.debug.tools.base.logging.Logger;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.util.Map;

public class AutowiredAnnotationProcessor {
    private static final Logger LOGGER = Logger.getLogger(AutowiredAnnotationProcessor.class);

    public static void processSingletonBeanInjection(DefaultListableBeanFactory beanFactory) {
        try {
            Map<String, AutowiredAnnotationBeanPostProcessor> postProcessors = beanFactory.getBeansOfType(
                AutowiredAnnotationBeanPostProcessor.class);
            if (postProcessors.isEmpty()) {
                LOGGER.debug("AutowiredAnnotationProcessor not exist");
                return;
            }
            AutowiredAnnotationBeanPostProcessor postProcessor = postProcessors.values().iterator().next();
            boolean postProcessPropertyValuesNotExists = false;
            for (String beanName : beanFactory.getBeanDefinitionNames()) {
                Object object = beanFactory.getSingleton(beanName);
                if (object != null) {
                    if (postProcessPropertyValuesNotExists) {
                        // spring 6.x
                        postProcessor.postProcessProperties(null, object, beanName);
                        continue;
                    }
                    try {
                        // from spring 3.2.x to 5.x
                        postProcessor.postProcessPropertyValues(null, null, object, beanName);
                    } catch (NoSuchMethodError e) {
                        // spring 6.x
                        postProcessor.postProcessProperties(null, object, beanName);
                        postProcessPropertyValuesNotExists = true;
                    }
                }
            }
        } catch (Throwable e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("AutowiredAnnotationProcessor maybe not exist", e);
            } else {
                LOGGER.warning("AutowiredAnnotationProcessor maybe not exist : " + e.getMessage());
            }
        }
    }

}
