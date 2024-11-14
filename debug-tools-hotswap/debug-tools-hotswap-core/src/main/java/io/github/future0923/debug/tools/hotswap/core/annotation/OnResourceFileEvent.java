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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.CREATE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.DELETE;
import static io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent.MODIFY;

/**
 * Event for a resource change (change of a file on the filesystem).
 * <p/>
 * Use with a non static method.
 * <p/>
 * Method attribute types:<ul>
 * <li>URI - URI of the watched resource</li>
 * <li>URL - URL of the watched resource</li>
 * <li>ClassLoader - the application classloader</li>
 * <li>ClassPool - initialized javassist classpool</li>
 * </ul>
 *
 * @author Jiri Bubnik
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
     * Filter watch event types. Default is all events (CREATE, MODIFY, DELETE).
     */
    FileEvent[] events() default {CREATE, MODIFY, DELETE};

    /**
     * Watch only for regular files. By default all other types (including directories) are filtered out.
     *
     * @return true to filter out other types than regular types.
     */
    public boolean onlyRegularFiles() default true;

    /**
     * Merge multiple same watch events up to this timeout into a single watch event (useful to merge multiple MODIFY events).
     */
    public int timeout() default 50;
}