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
package io.github.future0923.debug.tools.hotswap.core.annotation;

import io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchEventCommand;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.CREATE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.DELETE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.MODIFY;

/**
 * OnResourceFileEvent当资源文件改变时的事件（只能加到非静态方法上）
 * 方法上可以自动注入的参数类型如下，在{@link io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchEventCommand#executeCommand}中解析
 * <ul>
 * <li>{@link ClassLoader} - 加载class的类加载器
 * <li>{@link ClassPool} javassist的ClassPool
 * <li>{@link java.net.URI} watch resource的 URI
 * <li>{@link java.net.URL} - watch resource的 URL
 * </ul>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnResourceFileEvent {

    /**
     * 要watch的路径前缀
     */
    String path();

    /**
     * 正则表达式过滤到watch的资源文件
     */
    String filter() default "";

    /**
     * 感兴趣的类加载事件
     */
    FileEvent[] events() default {CREATE, MODIFY, DELETE};

    /**
     * 是否只watch常规文件
     */
    boolean onlyRegularFiles() default true;

    /**
     * 延迟执行命令的时间，{@link WatchEventCommand}可以合并
     */
    int timeout() default 50;
}