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
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * 处理插件方法中的{@link OnResourceFileEvent}和{@link OnClassFileEvent}
 */
public class WatchHandler<T extends Annotation> implements PluginHandler<T> {
    private static final Logger LOGGER = Logger.getLogger(WatchHandler.class);

    protected PluginManager pluginManager;

    public WatchHandler(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Override
    public boolean initField(PluginAnnotation<T> pluginAnnotation) {
        throw new IllegalAccessError("@OnResourceFileEvent or @OnClassFileEvent annotation not allowed on fields.");
    }


    @Override
    public boolean initMethod(final PluginAnnotation<T> pluginAnnotation) {
        LOGGER.debug("Init for method " + pluginAnnotation.getMethod());

        ClassLoader classLoader = pluginManager.getPluginRegistry().getAppClassLoader(pluginAnnotation.getPlugin());

        try {
            registerResources(pluginAnnotation, classLoader);
        } catch (IOException e) {
            LOGGER.error("Unable to register resources for annotation {} on method {} class {}", e,
                    pluginAnnotation.getAnnotation(),
                    pluginAnnotation.getMethod().getName(),
                    pluginAnnotation.getMethod().getDeclaringClass().getName());
            return false;
        }

        return true;
    }

    /**
     * 获取ClassLoader的resource下path信息，向watcher中path注册监听者
     */
    public void registerResources(final PluginAnnotation<T> pluginAnnotation, final ClassLoader classLoader) throws IOException {
        final T annot = pluginAnnotation.getAnnotation();
        WatchEventDTO watchEventDTO =  WatchEventDTO.parse(annot);
        // @OnClassFileEvent没有path
        String path = watchEventDTO.getPath();
        if (path == null || path.equals(".") || path.equals("/")) {
            path = "";
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 2);
        }
        // classpath resources 已经包括了extraClasspath
        // 当为OnClassFileEvent注解是，path为null，classLoader.getResources("")返回的为启动应用的 **/target/classes
        Enumeration<URL> en = classLoader.getResources(path);
        while (en.hasMoreElements()) {
            try {
                URI uri = en.nextElement().toURI();
                // 检查是否本地可以访问，不是vfs（Virtual File System）
                try {
                    new File(uri);
                } catch (Exception e) {
                    LOGGER.trace("Skipping uri {}, not a local file.", uri);
                    continue;
                }
                LOGGER.debug("Registering resource listener on classpath URI {}", uri);
                registerResourceListener(pluginAnnotation, watchEventDTO, classLoader, uri);
            } catch (URISyntaxException e) {
                LOGGER.error("Unable convert root resource path URL to URI", e);
            }
        }

        if (!watchEventDTO.isClassFileEvent()) {
            for (URL url : pluginManager.getPluginConfiguration(classLoader).getWatchResources()) {
                try {
                    Path watchResourcePath = Paths.get(url.toURI());
                    Path pathInWatchResource = watchResourcePath.resolve(path);
                    if (pathInWatchResource.toFile().exists()) {
                        LOGGER.debug("Registering resource listener on watchResources URI {}", pathInWatchResource.toUri());
                        registerResourceListener(pluginAnnotation, watchEventDTO, classLoader, pathInWatchResource.toUri());
                    }
                } catch (URISyntaxException e) {
                    LOGGER.error("Unable convert watch resource path URL {} to URI", e, url);
                }
            }
        }
    }

    /**
     * 注册资源变化的监听器，收到变化是创建命令并加入到调度器中等待命令的执行
     * <p>
     * WatchEventCommand是可以合并的，因此可以合并多个事件，减少命令执行次数
     */
    private void registerResourceListener(final PluginAnnotation<T> pluginAnnotation,
                                          final WatchEventDTO watchEventDTO,
                                          final ClassLoader classLoader,
                                          URI uri) throws IOException {
        pluginManager.getWatcher().addEventListener(classLoader, uri, event -> {
            WatchEventCommand<T> command = WatchEventCommand.createCmdForEvent(pluginAnnotation, event, classLoader);
            if (command != null) {
                pluginManager.getScheduler().scheduleCommand(command, watchEventDTO.getTimeout());
                LOGGER.trace("Resource changed {}", event);
            }
        });
    }

}
