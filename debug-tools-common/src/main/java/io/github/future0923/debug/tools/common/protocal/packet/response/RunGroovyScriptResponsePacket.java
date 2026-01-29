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
import io.github.future0923.debug.tools.common.enums.ResultClassType;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.EntityPacket;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author future0923
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class RunGroovyScriptResponsePacket extends EntityPacket<RunGroovyScriptResponsePacket> {

    private static final Logger logger = Logger.getLogger(RunGroovyScriptResponsePacket.class);

    private String applicationName;

    private ResultClassType resultClassType;

    private String printResult;

    private String throwable;

    private String offsetPath;

    @Override
    public byte getCommand() {
        return Command.RUN_GROOVY_SCRIPT_RESPONSE;
    }

    @Override
    public void doDeserialize(RunGroovyScriptResponsePacket packet) {
        this.setApplicationName(packet.getApplicationName());
        this.setResultClassType(packet.getResultClassType());
        this.setPrintResult(packet.getPrintResult());
        this.setThrowable(packet.getThrowable());
        this.setOffsetPath(packet.getOffsetPath());
    }

    public static RunGroovyScriptResponsePacket of(Throwable throwable, String offsetPath, String applicationName) {
        RunGroovyScriptResponsePacket packet = new RunGroovyScriptResponsePacket();
        packet.setApplicationName(applicationName);
        packet.setResultFlag(FAIL);
        packet.setThrowableMessage(throwable);
        packet.setOffsetPath(offsetPath);
        return packet;
    }

    public void setThrowableMessage(Throwable throwable) {
        setThrowable(ExceptionUtil.stacktraceToString(throwable, -1));
    }
}
