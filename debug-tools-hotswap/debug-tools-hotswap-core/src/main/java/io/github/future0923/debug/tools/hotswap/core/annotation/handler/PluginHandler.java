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
package io.github.future0923.debug.tools.hotswap.core.annotation.handler;

import java.lang.annotation.Annotation;

/**
 * 插件注解处理器
 */
public interface PluginHandler<T extends Annotation> {

    /**
     * 初始化字段注解
     *
     * @param pluginAnnotation 注解信息
     * @return 是否初始化成功
     */
    boolean initField(PluginAnnotation<T> pluginAnnotation);

    /**
     * 初始化方法注解
     *
     * @param pluginAnnotation 注解信息
     * @return 是否初始化成功
     */
    boolean initMethod(PluginAnnotation<T> pluginAnnotation);

}
