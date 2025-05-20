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
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.config.ClassLoaderInitListener;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * 注解{@link Init}处理器，类似spring @Autowired
 */
public class InitHandler implements PluginHandler<Init> {
    private static final Logger LOGGER = Logger.getLogger(InitHandler.class);

    protected PluginManager pluginManager;

    public InitHandler(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public boolean initField(PluginAnnotation<Init> pluginAnnotation) {
        Field field = pluginAnnotation.getField();

        // 为null则处理的时静态方法，用agentClassLoader
        // 否则用对应的类加载器
        ClassLoader appClassLoader = pluginAnnotation.getPlugin() == null ? getClass().getClassLoader() :
                pluginManager.getPluginRegistry().getAppClassLoader(pluginAnnotation.getPlugin());

        Object value = resolveType(appClassLoader, pluginAnnotation.getPluginClass(), field.getType());
        field.setAccessible(true);
        try {
            field.set(pluginAnnotation.getPlugin(), value);
        } catch (IllegalAccessException e) {
            LOGGER.error("Unable to set plugin field '{}' to value '{}' on plugin '{}'",
                    e, field.getName(), value, pluginAnnotation.getPluginClass());
            return false;
        }

        return true;
    }

    /**
     * 通过@Init初始化主插件
     * 如果是静态方法，则在{@link PluginManager}注册ClassLoaderInitListener，在classloader初始化时，执行插件方法
     */
    @Override
    public boolean initMethod(PluginAnnotation<Init> pluginAnnotation) {
        Object plugin = pluginAnnotation.getPlugin();

        if (plugin == null) {
            if (Modifier.isStatic(pluginAnnotation.getMethod().getModifiers())) {
                return registerClassLoaderInit(pluginAnnotation);
            } else {
                return true;
            }
        } else {
            if (!Modifier.isStatic(pluginAnnotation.getMethod().getModifiers())) {
                ClassLoader appClassLoader = pluginManager.getPluginRegistry().getAppClassLoader(plugin);
                return invokeInitMethod(pluginAnnotation, plugin, appClassLoader);
            } else {
                return true;
            }
        }
    }

    /**
     * 解析方法参数并调用方法
     *
     * @param pluginAnnotation 插件注解信息
     * @param plugin           插件对象（静态为null）
     * @param classLoader      所在的类加载器
     * @return 是否调用成功
     */
    private boolean invokeInitMethod(PluginAnnotation<Init> pluginAnnotation, Object plugin, ClassLoader classLoader) {
        List<Object> args = new ArrayList<>();
        for (Class<?> type : pluginAnnotation.getMethod().getParameterTypes()) {
            args.add(resolveType(classLoader, pluginAnnotation.getPluginClass(), type));
        }
        try {
            pluginAnnotation.getMethod().invoke(plugin, args.toArray());
            return true;
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException in init method on plugin {}.", e, pluginAnnotation.getPluginClass());
            return false;
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException in init method on plugin {}.", e, pluginAnnotation.getPluginClass());
            return false;
        }
    }

    /**
     * 在类加载器注册初始化事件-调用@Init静态方法。
     *
     * @param pluginAnnotation 插件注解信息
     */
    protected boolean registerClassLoaderInit(final PluginAnnotation<Init> pluginAnnotation) {
        LOGGER.debug("Registering ClassLoaderInitListener on {}", pluginAnnotation.getPluginClass());
        pluginManager.registerClassLoaderInitListener(new ClassLoaderInitListener() {
            @Override
            public void onInit(ClassLoader classLoader) {
                LOGGER.debug("Init plugin {} at classloader {}.", pluginAnnotation.getPluginClass(), classLoader);
                invokeInitMethod(pluginAnnotation, null, classLoader);
            }
        });
        return true;
    }

    /**
     * 通过class信息注入字段值，类似spring @Autowired
     *
     * @param classLoader 类加载器
     * @param pluginClass 插件的class信息
     * @param type        注入的类信息
     * @return 返回注入对象或null
     */
    protected Object resolveType(ClassLoader classLoader, Class<?> pluginClass, Class<?> type) {

        if (type.isAssignableFrom(PluginManager.class)) {
            return pluginManager;
        } else if (type.isAssignableFrom(Watcher.class)) {
            return pluginManager.getWatcher();
        } else if (type.isAssignableFrom(Scheduler.class)) {
            return pluginManager.getScheduler();
        } else if (type.isAssignableFrom(HotswapTransformer.class)) {
            return pluginManager.getHotswapTransformer();
        } else if (type.isAssignableFrom(PluginConfiguration.class)) {
            return pluginManager.getPluginConfiguration(classLoader);
        } else if (type.isAssignableFrom(ClassLoader.class)) {
            return classLoader;
        } else if (type.isAssignableFrom(Instrumentation.class)) {
            return pluginManager.getInstrumentation();
        } else {
            LOGGER.error("Unable process @Init on plugin '{}'." +
                    " Type '" + type + "' is not recognized for @Init annotation.", pluginClass);
            return null;
        }
    }
}
