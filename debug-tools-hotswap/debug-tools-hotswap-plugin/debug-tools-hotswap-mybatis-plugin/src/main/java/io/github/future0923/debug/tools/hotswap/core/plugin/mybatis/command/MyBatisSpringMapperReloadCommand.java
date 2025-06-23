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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.EventMergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisSpringMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisSpringPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringMapperReload;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher.MyBatisSpringMapperWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.util.Objects;

/**
 * 重载 MyBatis Spring mapper 类命令
 *
 * @author future0923
 */
public class MyBatisSpringMapperReloadCommand extends EventMergeableCommand<MyBatisSpringMapperReloadCommand> {

    private static final Logger logger = Logger.getLogger(MyBatisSpringMapperReloadCommand.class);

    private final ClassLoader userClassLoader;

    private final String className;

    private final byte[] bytes;

    private final String path;

    private WatchFileEvent event;

    /**
     * 当class重新定义时，通过{@link MyBatisSpringPatcher#redefineMyBatisSpringMapper(Class, ClassLoader, byte[])}创建命令后调用这
     */
    public MyBatisSpringMapperReloadCommand(ClassLoader userClassLoader, String className, byte[] bytes, String path) {
        this.userClassLoader = userClassLoader;
        this.className = className;
        this.bytes = bytes;
        this.path = path;
    }

    /**
     * 当class新增时，通过{@link MyBatisSpringMapperWatchEventListener#onEvent(WatchFileEvent)}创建命令后调用这
     */
    public MyBatisSpringMapperReloadCommand(ClassLoader userClassLoader, String className, byte[] bytes, String path, WatchFileEvent event) {
        this.userClassLoader = userClassLoader;
        this.className = className;
        this.bytes = bytes;
        this.path = path;
        this.event = event;
    }

    @Override
    protected WatchFileEvent event() {
        return event;
    }

    @Override
    public void executeCommand() {
        if (isDeleteEvent()) {
            logger.trace("Skip reload for delete event on class '{}'", className);
            return;
        }
        try {
            ClassLoader orginalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(userClassLoader);
            Class<?> reloadClass = userClassLoader.loadClass(MyBatisSpringMapperReload.class.getName());
            ReflectUtil.invoke(ReflectUtil.newInstance(reloadClass), "reload", ReflectUtil.newInstance(userClassLoader.loadClass(MyBatisSpringMapperReloadDTO.class.getName()), className, bytes, path));
            Thread.currentThread().setContextClassLoader(orginalClassLoader);
        } catch (Exception e) {
            logger.error("reloadConfiguration error", e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MyBatisSpringMapperReloadCommand)) return false;
        MyBatisSpringMapperReloadCommand that = (MyBatisSpringMapperReloadCommand) o;
        return Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }
}
