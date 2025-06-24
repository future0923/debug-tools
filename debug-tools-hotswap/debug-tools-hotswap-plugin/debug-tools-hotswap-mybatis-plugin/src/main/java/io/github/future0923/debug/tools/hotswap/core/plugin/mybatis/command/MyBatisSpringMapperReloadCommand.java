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
