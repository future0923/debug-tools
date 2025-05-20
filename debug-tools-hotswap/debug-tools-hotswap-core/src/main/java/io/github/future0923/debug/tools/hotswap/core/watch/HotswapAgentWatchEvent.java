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
package io.github.future0923.debug.tools.hotswap.core.watch;

import java.nio.file.WatchEvent;

/**
 * 热重载Agent事件
 */
public class HotswapAgentWatchEvent<T> implements WatchEvent<T> {
    private final Kind<T> kind;
    private final T context;

    private int count;

    public HotswapAgentWatchEvent(Kind<T> type, T context) {
        this.kind = type;
        this.context = context;
        this.count = 1;
    }

    @Override
    public Kind<T> kind() {
        return kind;
    }

    @Override
    public T context() {
        return context;
    }

    @Override
    public int count() {
        return count;
    }

    void increment() {
        count++;
    }
}
