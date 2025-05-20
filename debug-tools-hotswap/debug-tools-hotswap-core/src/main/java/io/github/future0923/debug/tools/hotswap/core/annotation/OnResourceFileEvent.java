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

import io.github.future0923.debug.tools.hotswap.core.annotation.handler.PluginAnnotation;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchEventCommand;
import io.github.future0923.debug.tools.hotswap.core.annotation.handler.WatchHandler;
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
 * 方法在{@link WatchHandler#initMethod(PluginAnnotation)}中注册
 * 方法上可以自动注入的参数类型如下，在{@link WatchEventCommand#executeCommand}中解析
 * <ul>
 * <li>{@link ClassLoader} 加载class的类加载器
 * <li>{@link ClassPool} javassist的ClassPool
 * <li>{@link java.net.URI} watch resource的 URI
 * <li>{@link java.net.URL} watch resource的 URL
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