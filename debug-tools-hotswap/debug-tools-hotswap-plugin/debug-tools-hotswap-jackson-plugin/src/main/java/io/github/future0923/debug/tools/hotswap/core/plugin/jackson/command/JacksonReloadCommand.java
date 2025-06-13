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
package io.github.future0923.debug.tools.hotswap.core.plugin.jackson.command;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializerCache;
import com.fasterxml.jackson.databind.ser.SerializerCache;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import io.github.future0923.debug.tools.vm.JvmToolsUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class JacksonReloadCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(JacksonReloadCommand.class);

    private final Class<?> clazz;

    public JacksonReloadCommand(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void executeCommand() {
        JavaType javaType = new ObjectMapper().constructType(clazz);
        boolean changed = false;
        ObjectMapper[] objectMappers = JvmToolsUtils.getInstances(ObjectMapper.class);
        for (ObjectMapper objectMapper : objectMappers) {
            ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = (ConcurrentHashMap<JavaType, JsonDeserializer<Object>>) ReflectionHelper.getNoException(objectMapper, ObjectMapper.class, "_rootDeserializers");
            if (_rootDeserializers != null) {
                boolean remove = _rootDeserializers.remove(javaType) != null;
                if (!changed && remove) {
                    changed = true;
                }
            }
        }
        DeserializerCache[] deserializerCaches = JvmToolsUtils.getInstances(DeserializerCache.class);
        for (DeserializerCache deserializerCache : deserializerCaches) {
            deserializerCache.flushCachedDeserializers();
            HashMap<JavaType, JsonDeserializer<Object>> _incompleteDeserializers = (HashMap<JavaType, JsonDeserializer<Object>>) ReflectionHelper.getNoException(deserializerCache, DeserializerCache.class, "_incompleteDeserializers");
            if (_incompleteDeserializers != null) {
                _incompleteDeserializers.remove(javaType);
            }
        }
        SerializerCache[] serializerCaches = JvmToolsUtils.getInstances(SerializerCache.class);
        for (SerializerCache serializerCache : serializerCaches) {
            serializerCache.flush();
        }
        if (changed) {
            logger.reload("Class '{}' has been reloaded.", clazz.getName());
        }
    }
}
