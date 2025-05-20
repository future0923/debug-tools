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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusEntityReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils.MyBatisUtils;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer.SpringBeanWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.io.IOException;
import java.util.Objects;

/**
 * @author future0923
 */
public class MyBatisPlusEntityWatchEventListener implements WatchEventListener {

    private static final Logger logger = Logger.getLogger(SpringBeanWatchEventListener.class);

    private final Scheduler scheduler;

    private final ClassLoader appClassLoader;

    private final String basePackage;

    public MyBatisPlusEntityWatchEventListener(Scheduler scheduler, ClassLoader appClassLoader, String basePackage) {
        this.scheduler = scheduler;
        this.appClassLoader = appClassLoader;
        this.basePackage = basePackage;
    }

    @Override
    public void onEvent(WatchFileEvent event) {
        logger.debug("{}, {}", event.getEventType(), event.getURI().toString());
        // 创建了class新文件
        if (FileEvent.CREATE.equals(event.getEventType()) && event.isFile() && event.getURI().toString().endsWith(".class")) {
            String className;
            try {
                className = IOUtils.urlToClassName(event.getURI());
            } catch (IOException e) {
                logger.trace("Watch event on resource '{}' skipped, probably Ok because of delete/create event sequence (compilation not finished yet).", e, event.getURI());
                return;
            }
            Class<?> clazz;
            try {
                clazz = appClassLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                logger.warning("not found class", e);
                return;
            }
            if (MyBatisUtils.isMyBatisPlusEntity(appClassLoader, clazz)) {
                scheduler.scheduleCommand(new MyBatisPlusEntityReloadCommand(appClassLoader, clazz), 500);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyBatisPlusEntityWatchEventListener that = (MyBatisPlusEntityWatchEventListener) o;
        return Objects.equals(appClassLoader, that.appClassLoader) && Objects.equals(basePackage, that.basePackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appClassLoader, basePackage);
    }
}
