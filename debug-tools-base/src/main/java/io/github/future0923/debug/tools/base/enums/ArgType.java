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
package io.github.future0923.debug.tools.base.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ArgType {

    TCP_PORT("tp", "tcp-port", true, "target application server tcp port\ndefault get available port."),
    HTTP_PORT("hp", "http-port", true, "target application server http port\ndefault get available port."),
    PROCESS_ID("pid", "pid", true, "target application process id"),
    ;

    private final String opt;

    private final String longOpt;

    private final boolean hasArg;

    private final String description;

    ArgType(String opt, String longOpt, boolean hasArg, String description) {
        this.opt = opt;
        this.longOpt = longOpt;
        this.hasArg = hasArg;
        this.description = description;
    }
}
