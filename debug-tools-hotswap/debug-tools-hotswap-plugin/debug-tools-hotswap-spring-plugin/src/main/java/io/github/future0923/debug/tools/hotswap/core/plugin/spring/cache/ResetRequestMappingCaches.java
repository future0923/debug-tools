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
package io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache;

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
