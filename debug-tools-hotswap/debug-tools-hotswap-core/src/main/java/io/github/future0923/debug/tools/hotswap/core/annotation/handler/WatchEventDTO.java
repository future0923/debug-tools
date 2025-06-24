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
package io.github.future0923.debug.tools.hotswap.core.annotation.handler;

import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;
import lombok.Getter;

import java.lang.annotation.Annotation;

/**
 * 解析{@link io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent}和{@link io.github.future0923.debug.tools.hotswap.core.annotation.OnClassFileEvent}注解信息
 */
@Getter
public class WatchEventDTO {

    /**
     * 是否是Class文件事件
     */
    private final boolean classFileEvent;

    /**
     * 延迟时间
     */
    private final int timeout;

    /**
     * watch的文件事件
     */
    private final FileEvent[] events;

    /**
     * 匹配类型的正则表达式
     */
    private final String classNameRegexp;

    /**
     * 正则表达式过滤到watch的资源文件
     */
    private final String filter;
    private final String path;
    private final boolean onlyRegularFiles;

    /**
     * 解析注解信息
     */
    public static <T extends Annotation> WatchEventDTO parse(T annotation) {
        if (annotation instanceof OnClassFileEvent)
            return new WatchEventDTO((OnClassFileEvent) annotation);
        else if (annotation instanceof OnResourceFileEvent)
            return new WatchEventDTO((OnResourceFileEvent) annotation);
        else
            throw new IllegalArgumentException("Invalid annotation type " + annotation);
    }

    public WatchEventDTO(OnClassFileEvent annotation) {
        classFileEvent = true;
        timeout = annotation.timeout();
        classNameRegexp = annotation.classNameRegexp();
        events = annotation.events();
        onlyRegularFiles = true;
        filter = null;
        path = null;
    }

    public WatchEventDTO(OnResourceFileEvent annotation) {
        classFileEvent = false;
        timeout = annotation.timeout();
        filter = annotation.filter();
        path = annotation.path();
        events = annotation.events();
        onlyRegularFiles = annotation.onlyRegularFiles();
        classNameRegexp = null;
    }

    /**
     * 是否支持WatchFileEvent事件处理
     *
     * @param event 文件事件
     * @return 是否支持
     */
    public boolean accept(WatchFileEvent event) {

        // 不是文件不处理
        if (!event.isFile()) {
            return false;
        }

        // load class files only from files named ".class"
        // Don't treat _jsp.class as a class file. JSP class files are compiled by application server, compilation
        // has two phases that cause many problems with HA. Look at JSR45
        if (isClassFileEvent() && (!event.getURI().toString().endsWith(".class") || event.getURI().toString().endsWith("_jsp.class"))) {
            return false;
        }

        return true;
    }
}
