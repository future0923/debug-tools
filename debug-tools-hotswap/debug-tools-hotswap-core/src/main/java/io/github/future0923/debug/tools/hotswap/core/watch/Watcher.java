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
package io.github.future0923.debug.tools.hotswap.core.watch;

import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;

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
     * 在类加载器中注册事件监听者，还会同步在 {@link PluginConfiguration#getExtraClasspath()} 注册
     *
     * @param classLoader 与路径关联的类加载器
     * @param basePackage 启动时基础的包名
     * @param pathPrefix  插件的路径
     * @param listener    监听者信息
     */
    void addEventListener(ClassLoader classLoader, String basePackage, URL pathPrefix, WatchEventListener listener);

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
