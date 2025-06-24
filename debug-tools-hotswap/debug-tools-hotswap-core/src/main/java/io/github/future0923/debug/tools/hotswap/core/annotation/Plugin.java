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
package io.github.future0923.debug.tools.hotswap.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插件
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Plugin {

    /**
     * 名称
     */
    String name() default "";

    /**
     * 描述
     */
    String description() default "";

    /**
     * 设置多个插件属于一个组，当这个组中的插件都没有匹配，那么会使用同一个组中{@link #fallback()}为true的
     */
    String group() default "";

    /**
     * 如果想同的{@link #group()}中没有其他插件匹配并且兜底设置为true，则使用此插件
     */
    boolean fallback() default false;

    /**
     * 测试该框架的目标框架版本，。
     */
    String[] testedVersions();

    /**
     * 此框架应使用的目标框架版本。
     */
    String[] expectedVersions() default {};

    /**
     * 除了插件本身支持转换，还可以指定其他的class填加{@link OnClassLoadEvent}和{@link OnResourceFileEvent}注解可以监听到对应的变化进行处理
     */
    Class<?>[] supportClass() default {};

}
