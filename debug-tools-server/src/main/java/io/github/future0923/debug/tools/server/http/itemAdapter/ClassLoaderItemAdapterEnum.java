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
package io.github.future0923.debug.tools.server.http.itemAdapter;

import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import lombok.Getter;

import java.util.function.Function;

/**
 *
 * @author iwillmissu
 * @date 2025/9/24 16:21:02
 **/
public enum ClassLoaderItemAdapterEnum {

    DEFAULT("", AllClassLoaderRes.Item::new),

    TOMCAT("org.apache.catalina.loader.ParallelWebappClassLoader", classLoader -> {
        String contextName = "";
        try {
            contextName = ReflectUtil.invoke(classLoader, "getContextName");
            return new AllClassLoaderRes.Item(classLoader.getClass().getName() + "[" + contextName + "]",
                    Integer.toHexString(System.identityHashCode(classLoader)));
        } catch (Exception e) {
            LoggerHolder.LOGGER.warning("tomcat classloader get context name failed,use default");
        }
        return DEFAULT.getAdapter().apply(classLoader);
    }),

    ;

    @Getter
    final String classLoaderName;
    @Getter
    final Function<ClassLoader, AllClassLoaderRes.Item> adapter;



    ClassLoaderItemAdapterEnum(String classLoaderName, final Function<ClassLoader, AllClassLoaderRes.Item> adapter) {
        this.classLoaderName = classLoaderName;
        this.adapter = adapter;
    }

    private static class LoggerHolder {
        static final Logger LOGGER = Logger.getLogger(ClassLoaderItemAdapterEnum.class);
    }

}
