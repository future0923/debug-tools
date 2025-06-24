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

import io.github.future0923.debug.tools.common.utils.DebugToolsTypeUtils;
import io.github.future0923.debug.tools.idea.listener.data.event.DataEvent;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class DefaultDataEventMulticaster implements DataEventMulticaster {

    private final Map<String, List<DataListener>> cacheTypeListenerMap = new ConcurrentHashMap<>();

    @Override
    public void addListener(DataListener listener) {
        String typeName = DebugToolsTypeUtils.getTypeArgument(listener.getClass()).getTypeName();
        List<DataListener> dataListeners = cacheTypeListenerMap.computeIfAbsent(typeName, k -> new LinkedList<>());
        dataListeners.add(listener);
    }

    @Override
    public void removeListener(DataListener listener) {
        String typeName = DebugToolsTypeUtils.getTypeArgument(listener.getClass()).getTypeName();
        cacheTypeListenerMap.computeIfPresent(typeName, (k,v) -> {
            v.remove(listener);
            return v;
        });
    }

    @Override
    public void multicastEvent(DataEvent event) {
        cacheTypeListenerMap.getOrDefault(event.getClass().getTypeName(), Collections.emptyList()).forEach(dataListener -> dataListener.event(event));
    }
}
