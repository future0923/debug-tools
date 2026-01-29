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

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.common.protocal.Command;
import io.github.future0923.debug.tools.common.protocal.packet.EntityPacket;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author future0923
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HotDeployResponsePacket extends EntityPacket<HotDeployResponsePacket> {

    private static final Logger logger = Logger.getLogger(HotDeployResponsePacket.class);

    private String applicationName;

    private String printResult;

    @Override
    public byte getCommand() {
        return Command.REMOTE_COMPILER_HOT_DEPLOY_RESPONSE;
    }

    @Override
    public void doDeserialize(HotDeployResponsePacket packet) {
        this.setApplicationName(packet.getApplicationName());
        this.setPrintResult(packet.getPrintResult());
    }

    public static HotDeployResponsePacket of(boolean isSuccess, String printResult, String applicationName) {
        HotDeployResponsePacket packet = new HotDeployResponsePacket();
        packet.setResultFlag(isSuccess ? SUCCESS : FAIL);
        packet.setApplicationName(applicationName);
        packet.setPrintResult(printResult);
        return packet;
    }

}
