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
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;

/**
 * 注册{@link PluginClassFileTransformer}到{@link HotswapTransformer}中，当class transform时{@link PluginClassFileTransformer}可以调用对应含有{@link OnClassLoadEvent}注解的方法
 */
public class OnClassLoadedHandler implements PluginHandler<OnClassLoadEvent> {

    protected static Logger LOGGER = Logger.getLogger(OnClassLoadedHandler.class);

    protected PluginManager pluginManager;

    protected HotswapTransformer hotswapTransformer;

    public OnClassLoadedHandler(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.hotswapTransformer = pluginManager.getHotswapTransformer();

        if (hotswapTransformer == null) {
            throw new IllegalArgumentException("Error instantiating OnClassLoadedHandler. Hotswap transformer is missing in PluginManager.");
        }
    }

    @Override
    public boolean initField(PluginAnnotation<OnClassLoadEvent> pluginAnnotation) {
        throw new IllegalAccessError("@OnClassLoadEvent annotation not allowed on fields.");
    }

    @Override
    public boolean initMethod(final PluginAnnotation<OnClassLoadEvent> pluginAnnotation) {
        LOGGER.debug("Init for method " + pluginAnnotation.getMethod());

        if (hotswapTransformer == null) {
            LOGGER.error("Error in init for method " + pluginAnnotation.getMethod() + ". Hotswap transformer is missing.");
            return false;
        }

        final OnClassLoadEvent annot = pluginAnnotation.getAnnotation();

        if (annot == null) {
            LOGGER.error("Error in init for method " + pluginAnnotation.getMethod() + ". Annotation missing.");
            return false;
        }

        ClassLoader appClassLoader = null;
        if (pluginAnnotation.getPlugin() != null){
            appClassLoader = pluginManager.getPluginRegistry().getAppClassLoader(pluginAnnotation.getPlugin());
        }

        hotswapTransformer.registerTransformer(appClassLoader, annot.classNameRegexp(), new PluginClassFileTransformer(pluginManager, pluginAnnotation));
        return true;
    }
}
