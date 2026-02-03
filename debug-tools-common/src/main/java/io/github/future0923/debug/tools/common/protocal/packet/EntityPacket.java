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
package io.github.future0923.debug.tools.common.protocal.packet;

import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author future0923
 */
public abstract class EntityPacket<T extends Packet> extends Packet{

    private static final Logger logger = Logger.getLogger(EntityPacket.class);

    public final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public EntityPacket() {
        this.entityClass = (Class<T>) ClassUtil.getTypeArgument(this.getClass());
    }

    @Override
    public void binarySerialize(ByteBuf byteBuf) {
        byte[] jsonBytes = DebugToolsJsonUtils
                .toJsonStr(this)
                .getBytes(StandardCharsets.UTF_8);
        byteBuf.writeBytes(jsonBytes);
    }

    @Override
    public void binaryDeserialization(ByteBuf in) {
        if (!in.isReadable()) {
            return;
        }
        String jsonString = in.toString(StandardCharsets.UTF_8);
        if (!DebugToolsJsonUtils.isTypeJSON(jsonString)) {
            logger.warning("The data {} received is not JSON, {}", this.getClass().getSimpleName(), jsonString);
            return;
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(entityClass.getClassLoader());
        doDeserialize(DebugToolsJsonUtils.toBean(jsonString, entityClass));
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    public abstract void doDeserialize(T packet);
}
