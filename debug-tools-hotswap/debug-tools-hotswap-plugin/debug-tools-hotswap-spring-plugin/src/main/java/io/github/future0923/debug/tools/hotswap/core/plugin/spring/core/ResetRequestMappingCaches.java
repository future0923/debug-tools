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
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 刷新Spring的Mapping缓存
 */
public class ResetRequestMappingCaches {

    private static final Logger LOGGER = Logger.getLogger(ResetRequestMappingCaches.class);

    private static Class<?> getHandlerMethodMappingClassOrNull() {
        try {
            return Class.forName("org.springframework.web.servlet.handler.AbstractHandlerMethodMapping");
        } catch (ClassNotFoundException e) {
            LOGGER.trace("HandlerMethodMapping class not found");
            return null;
        }
    }

    /**
     * 刷新Spring的Mapping映射
     */
    public static void reset(DefaultListableBeanFactory beanFactory) {
        Class<?> abstractHandlerMethodMapping = getHandlerMethodMappingClassOrNull();
        if (abstractHandlerMethodMapping == null) {
            return;
        }
        Map<String, ?> mappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, abstractHandlerMethodMapping, true, false);
        if (mappings.isEmpty()) {
            LOGGER.trace("Spring: no HandlerMappings found");
        }
        try {
            for (Entry<String, ?> e : mappings.entrySet()) {
                Object am = e.getValue();
                LOGGER.trace("Spring: clearing HandlerMapping for {}", am.getClass());
                try {
                    Field handlerMethods = abstractHandlerMethodMapping.getDeclaredField("handlerMethods");
                    handlerMethods.setAccessible(true);
                    ((Map<?,?>)handlerMethods.get(am)).clear();
                    Field urlMap = abstractHandlerMethodMapping.getDeclaredField("urlMap");
                    urlMap.setAccessible(true);
                    ((Map<?,?>)urlMap.get(am)).clear();
                    try {
                        Field nameMap = abstractHandlerMethodMapping.getDeclaredField("nameMap");
                        nameMap.setAccessible(true);
                        ((Map<?,?>)nameMap.get(am)).clear();
                    } catch(NoSuchFieldException noSuchFieldException) {
                        LOGGER.trace("Probably using Spring 4.0 or below: {}", noSuchFieldException.getMessage());
                    }
                } catch(NoSuchFieldException noSuchFieldException) {
                    LOGGER.trace("Probably using Spring 4.2+", noSuchFieldException.getMessage());
                    Method getHandlerMethods = abstractHandlerMethodMapping.getDeclaredMethod("getHandlerMethods", new Class[0]);
                    Class<?>[] parameterTypes = new Class[1];
                    parameterTypes[0] = Object.class;
                    Method unregisterMapping = abstractHandlerMethodMapping.getDeclaredMethod("unregisterMapping", parameterTypes);
                    Map<?,?> unmodifiableHandlerMethods = (Map<?,?>) getHandlerMethods.invoke(am);
                    Object[] keys = unmodifiableHandlerMethods.keySet().toArray();
                    for (Object key : keys) {
                        LOGGER.trace("Unregistering handler method {}", key);
                        unregisterMapping.invoke(am, key);
                    }
                }
                if (am instanceof InitializingBean) {
                    ((InitializingBean) am).afterPropertiesSet();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to clear HandlerMappings", e);
        }

    }

}
