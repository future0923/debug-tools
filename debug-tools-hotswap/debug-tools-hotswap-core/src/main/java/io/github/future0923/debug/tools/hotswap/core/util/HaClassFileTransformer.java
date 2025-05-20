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
package io.github.future0923.debug.tools.hotswap.core.util;

import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;

import java.lang.instrument.ClassFileTransformer;

/**
 * 热重载类文件Transformer
 */
public interface HaClassFileTransformer extends ClassFileTransformer {

    /**
     * 是否只关注{@link LoadEvent#REDEFINE}事件
     */
    boolean isForRedefinitionOnly();

}
