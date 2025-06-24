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
