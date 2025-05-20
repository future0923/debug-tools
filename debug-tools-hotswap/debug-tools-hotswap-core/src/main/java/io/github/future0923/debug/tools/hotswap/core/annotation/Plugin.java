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
