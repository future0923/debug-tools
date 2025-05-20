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
package io.github.future0923.debug.tools.hotswap.core.plugin.proxy.command;

import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.plugin.proxy.api.MultistepProxyTransformer;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

/**
 * 为 MultistepProxyTransformer 安排一个新的重定义事件。
 *
 * @author future0923
 */
public class RedefinitionScheduler implements Runnable{

    private final MultistepProxyTransformer transformer;

    @Init
    private static Instrumentation instrumentation;

    public RedefinitionScheduler(MultistepProxyTransformer transformer) {
        this.transformer = transformer;
    }

    @Override
    public void run() {
        try {
            instrumentation.redefineClasses(new ClassDefinition(transformer.getClassBeingRedefined(), transformer.getClassfileBuffer()));
        } catch (Throwable t) {
            transformer.removeClassState();
            throw new RuntimeException(t);
        }
    }

    public static void schedule(MultistepProxyTransformer multistepProxyTransformer) {
        new Thread(new RedefinitionScheduler(multistepProxyTransformer)).start();
    }
}
