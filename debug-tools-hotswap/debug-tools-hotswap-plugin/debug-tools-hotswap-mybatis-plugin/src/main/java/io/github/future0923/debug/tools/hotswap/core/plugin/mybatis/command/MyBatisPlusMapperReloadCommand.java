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
        System.out.println("aaaaaaaaa:" + System.currentTimeMillis());
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
        int i = Objects.hashCode(clazz);
        System.out.println("123213213213213 " + i);
        return i;
    }
}
