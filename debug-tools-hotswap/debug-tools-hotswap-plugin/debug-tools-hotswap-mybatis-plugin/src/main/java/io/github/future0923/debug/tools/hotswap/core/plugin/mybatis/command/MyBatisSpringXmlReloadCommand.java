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
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringXmlReload;

import java.net.URL;
import java.util.Objects;

/**
 * 重载 MyBatis Spring xml 文件命令
 *
 * @author future0923
 */
public class MyBatisSpringXmlReloadCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(MyBatisSpringXmlReloadCommand.class);

    private final ClassLoader userClassLoader;

    private final URL url;

    /**
     * 当xml文件变化时，通过{@link MyBatisPlugin#watchResource(URL, FileEvent)}创建MyBatisXmlResourceRefreshCommands后调用这里
     */
    public MyBatisSpringXmlReloadCommand(ClassLoader userClassLoader, URL url) {
        this.userClassLoader = userClassLoader;
        this.url = url;
    }

    @Override
    public void executeCommand() {
        try {
            ClassLoader orginalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(userClassLoader);
            Class<?> reloadClass = userClassLoader.loadClass(MyBatisSpringXmlReload.class.getName());
            ReflectUtil.invoke(ReflectUtil.newInstance(reloadClass), "reload", url);
            Thread.currentThread().setContextClassLoader(orginalClassLoader);
        } catch (Exception e) {
            logger.error("reload MyBatis spring xml error", e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof MyBatisSpringXmlReloadCommand)) return false;

        MyBatisSpringXmlReloadCommand that = (MyBatisSpringXmlReloadCommand) o;
        return Objects.equals(url.getPath(), that.url.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(url.getPath());
    }
}
