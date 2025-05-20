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
package io.github.future0923.debug.tools.hotswap.core.annotation.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析插件上的注解并注册对应的处理器
 */
public class AnnotationProcessor {

    private static final Logger LOGGER = Logger.getLogger(AnnotationProcessor.class);

    public AnnotationProcessor(PluginManager pluginManager) {
        init(pluginManager);
    }

    private final Map<Class<? extends Annotation>, PluginHandler> handlers = new HashMap<>();

    public void init(PluginManager pluginManager) {
        addAnnotationHandler(Init.class, new InitHandler(pluginManager));
        addAnnotationHandler(OnClassLoadEvent.class, new OnClassLoadedHandler(pluginManager));
        addAnnotationHandler(OnClassFileEvent.class, new WatchHandler<OnClassFileEvent>(pluginManager));
        addAnnotationHandler(OnResourceFileEvent.class, new WatchHandler<OnResourceFileEvent>(pluginManager));
    }

    public void addAnnotationHandler(Class<? extends Annotation> annotation, PluginHandler handler) {
        handlers.put(annotation, handler);
    }

    /**
     * 处理插件类上的注解，仅处理static类型的字段和方法
     *
     * @param processClass 要处理的类信息
     * @param pluginClass  插件类信息
     *                     一个插件可能通过{@link Plugin#supportClass()}指定多个类文件要处理
     * @return 是否处理成功
     */
    public boolean processAnnotations(Class<?> processClass, Class<?> pluginClass) {

        try {
            for (Field field : processClass.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()))
                    if (!processFieldAnnotations(null, field, pluginClass))
                        return false;

            }

            for (Method method : processClass.getDeclaredMethods()) {
                if (Modifier.isStatic(method.getModifiers()))
                    if (!processMethodAnnotations(null, method, pluginClass))
                        return false;
            }

            // process annotations on all supporting classes in addition to the plugin itself
            for (Annotation annotation : processClass.getDeclaredAnnotations()) {
                if (annotation instanceof Plugin) {
                    for (Class<?> supportClass : ((Plugin) annotation).supportClass()) {
                        processAnnotations(supportClass, pluginClass);
                    }
                }
            }

            return true;
        } catch (Throwable e) {
            LOGGER.error("Unable to process plugin annotations '{}'", e, pluginClass);
            return false;
        }
    }

    /**
     * 处理插件类上的注解，仅处理非static类型的字段和方法
     *
     * @param plugin 插件对象
     * @return 是否处理成功
     */
    public boolean processAnnotations(Object plugin) {
        LOGGER.debug("Processing annotations for plugin '" + plugin + "'.");

        Class<?> pluginClass = plugin.getClass();

        for (Field field : pluginClass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (!processFieldAnnotations(plugin, field, pluginClass)) {
                    return false;
                }
            }
        }

        for (Method method : pluginClass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                if (!processMethodAnnotations(plugin, method, pluginClass)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 处理字段注解
     *
     * @param plugin      插件对象（静态时为null）
     * @param field       注解所在字段信息
     * @param pluginClass 插件类信息
     * @return 是否处理成功
     */
    @SuppressWarnings("unchecked")
    private boolean processFieldAnnotations(Object plugin, Field field, Class<?> pluginClass) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
            for (Class<? extends Annotation> handlerAnnotation : handlers.keySet()) {
                if (annotation.annotationType().equals(handlerAnnotation)) {
                    PluginAnnotation<?> pluginAnnotation = new PluginAnnotation<>(pluginClass, plugin, annotation, field);
                    // 有一个handler初始化失败就退出，里面有失败日志
                    if (!handlers.get(handlerAnnotation).initField(pluginAnnotation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 处理方法注解
     *
     * @param plugin      插件对象（静态时为null）
     * @param method      注解所在方法信息
     * @param pluginClass 插件类信息
     * @return 是否处理成功
     */
    @SuppressWarnings("unchecked")
    private boolean processMethodAnnotations(Object plugin, Method method, Class pluginClass) {
        for (Annotation annotation : method.getDeclaredAnnotations()) {
            for (Class<? extends Annotation> handlerAnnotation : handlers.keySet()) {
                if (annotation.annotationType().equals(handlerAnnotation)) {
                    PluginAnnotation<?> pluginAnnotation = new PluginAnnotation<>(pluginClass, plugin, annotation, method);
                    if (!handlers.get(handlerAnnotation).initMethod(pluginAnnotation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
