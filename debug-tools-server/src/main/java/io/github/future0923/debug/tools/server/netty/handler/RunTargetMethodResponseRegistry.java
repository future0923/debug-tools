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
package io.github.future0923.debug.tools.server.netty.handler;

import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 保存已完成的方法调用响应，IDEA 断线重连后可按 request identity 补领最终响应。
 */
final class RunTargetMethodResponseRegistry {

    private final ConcurrentMap<String, RunTargetMethodResponsePacket> responses = new ConcurrentHashMap<>();

    void record(RunTargetMethodResponsePacket packet) {
        if (packet == null || DebugToolsStringUtils.isBlank(packet.getIdentity())) {
            return;
        }
        responses.put(packet.getIdentity(), packet);
    }

    Optional<RunTargetMethodResponsePacket> find(String identity) {
        if (DebugToolsStringUtils.isBlank(identity)) {
            return Optional.empty();
        }
        return Optional.ofNullable(responses.get(identity));
    }

    void remove(String identity) {
        if (DebugToolsStringUtils.isBlank(identity)) {
            return;
        }
        responses.remove(identity);
    }
}
