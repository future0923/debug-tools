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
package io.github.future0923.debug.tools.idea.listener.data;

import io.github.future0923.debug.tools.idea.listener.data.event.DataEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author future0923
 */
public class DefaultDataEventMulticaster implements DataEventMulticaster {

    // 简化为广播给所有监听器，监听器内部自行判断事件类型，避免泛型推断为空导致的 NPE
    private final List<DataListener> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void addListener(DataListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(DataListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void multicastEvent(DataEvent event) {
        for (DataListener listener : listeners) {
            try {
                listener.event(event);
            } catch (Throwable ignore) {
                // 单个监听器异常不影响其他监听器
            }
        }
    }
}
