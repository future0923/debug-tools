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
package io.github.future0923.debug.tools.hotswap.core.annotation;

import io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchEventCommand;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchHandler;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.URI;
import java.net.URL;

import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.CREATE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.DELETE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.MODIFY;

/**
 * OnClassFileEvent当java class文件改变时的事件（只能加到非静态方法上）
 * <p>
 * 原理为{@link WatchHandler#registerResources}解析这个注解时会{@link ClassLoader#getResources(String)}传入{@code ""}来获取到程序{@code **\/target\/classes}的路径，
 * 然后使用{@link Watcher}来监听该目录变化
 * <p>
 * 方法上可以自动注入的参数类型如下，在{@link WatchEventCommand#executeCommand}中解析
 * <ul>
 * <li>{@link ClassLoader} - 加载class的类加载器
 * <li>{@link String} 类名(e.g: {@code java/utils/List})
 * <li>{@link ClassPool} javassist的ClassPool
 * <li>{@link CtClass} 通过byte[]与ClassLoader创建的javassist的CtClass
 * <li>{@link URI} watch resource的 URI
 * <li>{@link URL} - watch resource的 URL
 * </ul>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnClassFileEvent {

    /**
     * 感兴趣的类正则表达式
     */
    String classNameRegexp();

    /**
     * 感兴趣的文件事件
     */
    FileEvent[] events() default {CREATE, MODIFY, DELETE};

    /**
     * 延迟执行命令的时间，{@link WatchEventCommand}可以合并
     */
    int timeout() default 50;
}
