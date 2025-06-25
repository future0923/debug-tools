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
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisPlusPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisPlusMapperReload;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher.MyBatisPlusMapperWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.util.Objects;

/**
 * 重载 MyBatis Plus Mapper 命令，支持新增
 *
 * @author future0923
 */
public class MyBatisPlusMapperReloadCommand extends EventMergeableCommand<MyBatisPlusMapperReloadCommand> {

    private static final Logger logger = Logger.getLogger(MyBatisPlusMapperReloadCommand.class);

    private final ClassLoader userClassLoader;

    private final Class<?> clazz;

    private final byte[] bytes;

    private final String path;

    private WatchFileEvent event;

    /**
     * 当class重新定义时，通过{@link MyBatisPlusPatcher#redefineMyBatisClass(Class, byte[])}创建命令后调用这
     */
    public MyBatisPlusMapperReloadCommand(ClassLoader userClassLoader, Class<?> clazz, byte[] bytes, String path) {
        this.userClassLoader = userClassLoader;
        this.clazz = clazz;
        this.bytes = bytes;
        this.path = path;
    }

    /**
     * 当class新增时，通过{@link MyBatisPlusMapperWatchEventListener#onEvent(WatchFileEvent)}创建命令后调用这
     */
    public MyBatisPlusMapperReloadCommand(ClassLoader userClassLoader, Class<?> clazz, byte[] bytes, String path, WatchFileEvent event) {
        this.userClassLoader = userClassLoader;
        this.clazz = clazz;
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
            logger.trace("Skip reload for delete event on class '{}'", clazz.getName());
            return;
        }
        try {
            ClassLoader orginalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(userClassLoader);
            Class<?> reloadClass = userClassLoader.loadClass(MyBatisPlusMapperReload.class.getName());
            ReflectUtil.invoke(ReflectUtil.newInstance(reloadClass), "reload", ReflectUtil.newInstance(userClassLoader.loadClass(MyBatisPlusMapperReloadDTO.class.getName()), userClassLoader, clazz, bytes, path));
            Thread.currentThread().setContextClassLoader(orginalClassLoader);
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MyBatisPlusMapperReloadCommand)) return false;

        MyBatisPlusMapperReloadCommand that = (MyBatisPlusMapperReloadCommand) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clazz);
    }
}
