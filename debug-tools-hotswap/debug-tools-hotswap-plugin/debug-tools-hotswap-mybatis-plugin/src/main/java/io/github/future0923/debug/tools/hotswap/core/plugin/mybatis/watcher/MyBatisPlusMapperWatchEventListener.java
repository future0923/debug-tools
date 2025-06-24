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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusMapperReloadCommand;
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
public class MyBatisPlusMapperWatchEventListener implements WatchEventListener {

    private static final Logger logger = Logger.getLogger(SpringBeanWatchEventListener.class);

    private final Scheduler scheduler;

    private final ClassLoader appClassLoader;

    private final String basePackage;

    public MyBatisPlusMapperWatchEventListener(Scheduler scheduler, ClassLoader appClassLoader, String basePackage) {
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
            if (MyBatisUtils.isMyBatisMapper(appClassLoader, clazz)) {
                byte[] bytes = IOUtils.toByteArray(event.getURI());
                scheduler.scheduleCommand(new MyBatisPlusMapperReloadCommand(appClassLoader, clazz, bytes, event.getURI().getPath(), event), 1000);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyBatisPlusMapperWatchEventListener that = (MyBatisPlusMapperWatchEventListener) o;
        return Objects.equals(appClassLoader, that.appClassLoader) && Objects.equals(basePackage, that.basePackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appClassLoader, basePackage);
    }
}
