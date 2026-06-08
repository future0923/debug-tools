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

import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.trace.MethodTrace;
import io.github.future0923.debug.tools.base.trace.MethodTreeNode;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.dto.RunResultDTO;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodResponsePacket;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodStreamEventType;
import io.github.future0923.debug.tools.common.protocal.packet.response.RunTargetMethodStreamResponsePacket;
import io.github.future0923.debug.tools.common.utils.DebugToolsJsonUtils;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.server.utils.DebugToolsResultUtils;
import io.netty.channel.ChannelHandlerContext;

import java.time.Duration;
import java.util.List;

final class RunTargetMethodResultWriter {

    void writeNormalResult(
            Object result,
            Long duration,
            RunDTO runDTO,
            ChannelHandlerContext ctx,
            boolean voidType,
            boolean traceMethod,
            RunTargetMethodResponseRegistry responseRegistry
    ) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO, DebugToolsBootstrap.serverConfig.getApplicationName());
        packet.setDuration(duration);
        if (voidType) {
            packet.setResultClassType(ResultClassType.VOID);
            packet.setPrintResult("Void");
        } else {
            if (result == null) {
                packet.setResultClassType(ResultClassType.NULL);
                packet.setPrintResult("NULL");
            } else if (ClassUtil.isSimpleValueType(result.getClass())) {
                packet.setResultClassType(ResultClassType.SIMPLE);
                packet.setPrintResult(Convert.toStr(result));
            } else {
                packet.setResultClassType(ResultClassType.OBJECT);
                packet.setPrintResult(result.toString());
                String offsetPath = RunResultDTO.genOffsetPathRandom(result);
                packet.setOffsetPath(offsetPath);
                DebugToolsResultUtils.putCache(offsetPath, result);
            }
        }
        if (traceMethod) {
            List<MethodTreeNode> traceResult = MethodTrace.getResult();
            String offsetPath = RunResultDTO.genOffsetPathRandom(traceResult);
            DebugToolsResultUtils.putCache(offsetPath, traceResult);
            packet.setTraceOffsetPath(offsetPath);
        }
        responseRegistry.record(packet);
        ctx.writeAndFlush(packet);
    }

    RunTargetMethodStreamResponsePacket createNextPacket(RunDTO runDTO, long sequence, Object value, Long duration) {
        RunTargetMethodStreamResponsePacket packet = createStreamPacket(
                runDTO,
                sequence,
                RunTargetMethodStreamEventType.NEXT,
                duration
        );
        Object data = unwrapServerSentEvent(value, packet);
        packet.setDataClassName(data == null ? null : data.getClass().getName());
        packet.setData(toDisplayData(data));
        return packet;
    }

    RunTargetMethodStreamResponsePacket createStreamPacket(
            RunDTO runDTO,
            long sequence,
            RunTargetMethodStreamEventType eventType,
            Long duration
    ) {
        RunTargetMethodStreamResponsePacket packet = new RunTargetMethodStreamResponsePacket();
        packet.setRunInfo(runDTO, DebugToolsBootstrap.serverConfig.getApplicationName());
        packet.setSequence(sequence);
        packet.setEventType(eventType);
        packet.setDuration(duration);
        return packet;
    }

    /**
     * Spring ServerSentEvent 在部分目标应用中可能存在，使用反射避免强绑定具体版本 API。
     */
    private Object unwrapServerSentEvent(Object value, RunTargetMethodStreamResponsePacket packet) {
        if (value == null || !"org.springframework.http.codec.ServerSentEvent".equals(value.getClass().getName())) {
            return value;
        }
        packet.setEvent(Convert.toStr(ReflectUtil.invoke(value, "event"), null));
        packet.setEventId(Convert.toStr(ReflectUtil.invoke(value, "id"), null));
        Object retry = ReflectUtil.invoke(value, "retry");
        packet.setRetry(retry instanceof Duration ? String.valueOf(((Duration) retry).toMillis()) : Convert.toStr(retry, null));
        packet.setComment(Convert.toStr(ReflectUtil.invoke(value, "comment"), null));
        return ReflectUtil.invoke(value, "data");
    }

    private String toDisplayData(Object data) {
        if (data == null) {
            return "null";
        }
        if (ClassUtil.isSimpleValueType(data.getClass())) {
            return Convert.toStr(data);
        }
        return DebugToolsJsonUtils.toJsonStr(data);
    }
}
