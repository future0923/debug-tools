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
