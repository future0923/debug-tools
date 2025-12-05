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
package io.github.future0923.debug.tools.hotswap.core.plugin.feign.command;

import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.EventMergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.FeignPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.reload.FeignClientReload;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.reload.FeignClientReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.watcher.FeignClientWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.util.Objects;

/**
 * 重载 MyBatis Spring mapper 类命令
 *
 * @author future0923
 */
public class FeignClientReloadCommand extends EventMergeableCommand<FeignClientReloadCommand> {

    private static final Logger logger = Logger.getLogger(FeignClientReloadCommand.class);

    private final ClassLoader userClassLoader;

    private final String className;

    private final byte[] bytes;

    private final String path;

    private WatchFileEvent event;

    /**
     * 当class重新定义时，通过{@link FeignPlugin#redefineFeignClass(Class, ClassLoader, byte[])}创建命令后调用这
     */
    public FeignClientReloadCommand(ClassLoader userClassLoader, String className, byte[] bytes, String path) {
        this.userClassLoader = userClassLoader;
        this.className = className;
        this.bytes = bytes;
        this.path = path;
    }

    /**
     * 当class新增时，通过{@link FeignClientWatchEventListener#onEvent(WatchFileEvent)}创建命令后调用这
     */
    public FeignClientReloadCommand(ClassLoader userClassLoader, String className, byte[] bytes, String path, WatchFileEvent event) {
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
            Class<?> reloadClass = userClassLoader.loadClass(FeignClientReload.class.getName());
            ReflectUtil.invoke(ReflectUtil.newInstance(reloadClass), "reload", ReflectUtil.newInstance(userClassLoader.loadClass(FeignClientReloadDTO.class.getName()), userClassLoader, className, bytes, path));
            Thread.currentThread().setContextClassLoader(orginalClassLoader);
        } catch (Exception e) {
            logger.error("reloadConfiguration error", e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof FeignClientReloadCommand)) return false;
        FeignClientReloadCommand that = (FeignClientReloadCommand) o;
        return Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }
}
