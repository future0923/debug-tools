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
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.dto.RunDTO;
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.EntityPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author future0923
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class RunTargetMethodResponsePacket extends EntityPacket<RunTargetMethodResponsePacket> {

    private static final Logger logger = Logger.getLogger(RunTargetMethodResponsePacket.class);

    private String identity;

    private String applicationName;

    private String classLoaderIdentity;

    private String className;

    private String methodName;

    private List<String> methodParameterTypes;

    private ResultClassType resultClassType;

    private String printResult;

    private String throwable;

    private String offsetPath;

    private String traceOffsetPath;

    private Long duration;

    @Override
    public byte getCommand() {
        return Command.RUN_TARGET_METHOD_RESPONSE;
    }

    @Override
    public void doDeserialize(RunTargetMethodResponsePacket packet) {
        this.setIdentity(packet.getIdentity());
        this.setDuration(packet.getDuration());
        this.setApplicationName(packet.getApplicationName());
        this.setClassLoaderIdentity(packet.getClassLoaderIdentity());
        this.setClassName(packet.getClassName());
        this.setMethodName(packet.getMethodName());
        this.setMethodParameterTypes(packet.getMethodParameterTypes());
        this.setResultClassType(packet.getResultClassType());
        this.setPrintResult(packet.getPrintResult());
        this.setThrowable(packet.getThrowable());
        this.setOffsetPath(packet.getOffsetPath());
        this.setTraceOffsetPath(packet.getTraceOffsetPath());
    }

    public static RunTargetMethodResponsePacket of(RunDTO runDTO, Throwable throwable, String offsetPath, String applicationName) {
        RunTargetMethodResponsePacket packet = new RunTargetMethodResponsePacket();
        packet.setRunInfo(runDTO, applicationName);
        packet.setResultFlag(FAIL);
        packet.setThrowableMessage(throwable);
        packet.setOffsetPath(offsetPath);
        return packet;
    }

    public void setThrowableMessage(Throwable throwable) {
        setThrowable(ExceptionUtil.stacktraceToString(throwable, -1));
    }

    public void setRunInfo(RunDTO runDTO, String applicationName) {
        this.setIdentity(runDTO.getIdentity());
        this.setApplicationName(applicationName);
        this.setClassLoaderIdentity(runDTO.getClassLoader() == null ? null : runDTO.getClassLoader().getName() + "@" + runDTO.getClassLoader().getIdentity());
        this.setClassName(runDTO.getTargetClassName());
        this.setMethodName(runDTO.getTargetMethodName());
        this.setMethodParameterTypes(runDTO.getTargetMethodParameterTypes());
    }
}
