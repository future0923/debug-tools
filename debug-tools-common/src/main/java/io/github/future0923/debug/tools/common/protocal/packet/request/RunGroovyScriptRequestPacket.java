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
package io.github.future0923.debug.tools.common.protocal.packet.request;

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
public class RunGroovyScriptRequestPacket extends EntityPacket<RunGroovyScriptRequestPacket> {

    private static final Logger logger = Logger.getLogger(RunGroovyScriptRequestPacket.class);

    /**
     * Groovy脚本内容
     */
    private String script;

    /**
     * 类加载器
     */
    private String identity;

    @Override
    public byte getCommand() {
        return Command.RUN_GROOVY_SCRIPT_REQUEST;
    }

    @Override
    public void doDeserialize(RunGroovyScriptRequestPacket packet) {
        this.setScript(packet.getScript());
        this.setIdentity(packet.getIdentity());
    }
}
