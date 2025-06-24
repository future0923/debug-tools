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
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusEntityReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisPlusPatcher;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisPlusEntityReload;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher.MyBatisPlusEntityWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.util.Objects;

/**
 * 重载 MyBatis Plus Entity 命令 AbstractSqlInjector.inspectInject
 *
 * @author future0923
 */
public class MyBatisPlusEntityReloadCommand extends EventMergeableCommand<MyBatisPlusEntityReloadCommand> {

    private static final Logger logger = Logger.getLogger(MyBatisPlusEntityReloadCommand.class);

    private final ClassLoader userClassLoader;

    private final Class<?> clazz;

    private WatchFileEvent event;

    /**
     * 当class重新定义时，通过{@link MyBatisPlusPatcher#redefineMyBatisClass(Class, byte[])}创建命令后调用这
     */
    public MyBatisPlusEntityReloadCommand(ClassLoader userClassLoader, Class<?> clazz) {
        this.userClassLoader = userClassLoader;
        this.clazz = clazz;
    }

    /**
     * 当class新增时，通过{@link MyBatisPlusEntityWatchEventListener#onEvent(WatchFileEvent)}创建命令后调用这
     */
    public MyBatisPlusEntityReloadCommand(ClassLoader userClassLoader, Class<?> clazz, WatchFileEvent event) {
        this.userClassLoader = userClassLoader;
        this.clazz = clazz;
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
            Class<?> reloadClass = userClassLoader.loadClass(MyBatisPlusEntityReload.class.getName());
            ReflectUtil.invoke(ReflectUtil.newInstance(reloadClass), "reload", ReflectUtil.newInstance(userClassLoader.loadClass(MyBatisPlusEntityReloadDTO.class.getName()), userClassLoader, clazz));
            Thread.currentThread().setContextClassLoader(orginalClassLoader);
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MyBatisPlusEntityReloadCommand)) return false;

        MyBatisPlusEntityReloadCommand that = (MyBatisPlusEntityReloadCommand) o;
        return Objects.equals(clazz, that.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clazz);
    }
}
