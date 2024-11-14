/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.watch;

import java.net.URI;
import java.net.URL;

/**
 * 目录树变化的观察者
 */
public interface Watcher {

    /**
     * 在类加载器中注册事件监听者
     *
     * @param classLoader 与路径关联的类加载器
     * @param pathPrefix  插件的路径
     * @param listener    监听者信息
     */
    void addEventListener(ClassLoader classLoader, URI pathPrefix, WatchEventListener listener);

    /**
     * 在类加载器中注册事件监听者
     *
     * @param classLoader 与路径关联的类加载器
     * @param pathPrefix  插件的路径
     * @param listener    监听者信息
     */
    void addEventListener(ClassLoader classLoader, URL pathPrefix, WatchEventListener listener);

    /**
     * 删除在类加载器中注册的所有侦听器
     */
    void closeClassLoader(ClassLoader classLoader);

    /**
     * 开始观察
     */
    void run();

    /**
     * 停止观察
     */
    void stop();
}
