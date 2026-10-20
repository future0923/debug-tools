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
import java.util.*;
import java.util.function.LongSupplier;

/** 完成响应最多保留512条/30分钟；缓存缺失不表示业务未执行。 */
final class RunTargetMethodResponseRegistry {
    private final Map<String,Entry> responses=new LinkedHashMap<>();
    private final LongSupplier clock;
    RunTargetMethodResponseRegistry() { this(System::currentTimeMillis); }
    RunTargetMethodResponseRegistry(LongSupplier clock) { this.clock=clock; }
    synchronized void record(RunTargetMethodResponsePacket packet) {
        if(packet==null || DebugToolsStringUtils.isBlank(packet.getIdentity()))return;
        cleanup();responses.put(packet.getIdentity(),new Entry(packet,clock.getAsLong()));
        while(responses.size()>512)responses.remove(responses.keySet().iterator().next());
    }
    synchronized Optional<RunTargetMethodResponsePacket> find(String identity) {
        if(DebugToolsStringUtils.isBlank(identity))return Optional.empty();
        cleanup();Entry entry=responses.get(identity);return Optional.ofNullable(entry==null?null:entry.packet);
    }
    synchronized void remove(String identity) { responses.remove(identity); }
    private void cleanup() { long cutoff=clock.getAsLong()-1800000;responses.values().removeIf(entry->entry.createdAt<cutoff); }
    private static final class Entry { final RunTargetMethodResponsePacket packet;final long createdAt;Entry(RunTargetMethodResponsePacket packet,long createdAt){this.packet=packet;this.createdAt=createdAt;} }
}
