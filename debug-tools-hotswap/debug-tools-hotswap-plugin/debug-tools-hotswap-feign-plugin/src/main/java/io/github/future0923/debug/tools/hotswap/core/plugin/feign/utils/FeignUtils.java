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
package io.github.future0923.debug.tools.hotswap.core.plugin.feign.utils;

import java.lang.annotation.Annotation;

/**
 * @author future0923
 */
public class FeignUtils {

    private static final Object reload_lock = new Object();

    /**
     * 判断是否是@FeignClient标记的接口
     * <p>
     * 1. 是一个接口
     * 2. 含有FeignClient注解
     */
    @SuppressWarnings("unchecked")
    public static boolean isFeignClient(ClassLoader loader, Class<?> clazz) {
        try {
            if (clazz.isInterface()
                    && clazz.getAnnotation((Class<? extends Annotation>) loader.loadClass("org.springframework.cloud.openfeign.FeignClient")) != null) {
                return true;
            }
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }
}
