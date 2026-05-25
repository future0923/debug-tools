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
package io.github.future0923.debug.tools.common.protocal.packet.response;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.ExceptionUtil;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.EntityPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 运行目标方法的 Flux/ServerSentEvent 分段响应。
 *
 * @author future0923
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class RunTargetMethodStreamResponsePacket extends EntityPacket<RunTargetMethodStreamResponsePacket> {

    private String identity;

    private String applicationName;

    private String classLoaderIdentity;

    private String className;

    private String methodName;

    private List<String> methodParameterTypes;

    private Long sequence;

    private RunTargetMethodStreamEventType eventType;

    private String data;

    private String dataClassName;

    private String event;

    private String eventId;

    private String retry;

    private String comment;

    private String throwable;

    private Long duration;

    @Override
    public byte getCommand() {
        return Command.RUN_TARGET_METHOD_STREAM_RESPONSE;
    }

    @Override
    public void doDeserialize(RunTargetMethodStreamResponsePacket packet) {
        this.setIdentity(packet.getIdentity());
        this.setApplicationName(packet.getApplicationName());
        this.setClassLoaderIdentity(packet.getClassLoaderIdentity());
        this.setClassName(packet.getClassName());
        this.setMethodName(packet.getMethodName());
        this.setMethodParameterTypes(packet.getMethodParameterTypes());
        this.setSequence(packet.getSequence());
        this.setEventType(packet.getEventType());
        this.setData(packet.getData());
        this.setDataClassName(packet.getDataClassName());
        this.setEvent(packet.getEvent());
        this.setEventId(packet.getEventId());
        this.setRetry(packet.getRetry());
        this.setComment(packet.getComment());
        this.setThrowable(packet.getThrowable());
        this.setDuration(packet.getDuration());
    }

    public void setRunInfo(RunDTO runDTO, String applicationName) {
        this.setIdentity(runDTO.getIdentity());
        this.setApplicationName(applicationName);
        this.setClassLoaderIdentity(runDTO.getClassLoader() == null ? null : runDTO.getClassLoader().getName() + "@" + runDTO.getClassLoader().getIdentity());
        this.setClassName(runDTO.getTargetClassName());
        this.setMethodName(runDTO.getTargetMethodName());
        this.setMethodParameterTypes(runDTO.getTargetMethodParameterTypes());
    }

    public void setThrowableMessage(Throwable throwable) {
        setThrowable(ExceptionUtil.stacktraceToString(throwable, -1));
    }
}
