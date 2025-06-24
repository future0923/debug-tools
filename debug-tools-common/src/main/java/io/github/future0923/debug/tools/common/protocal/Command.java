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
package io.github.future0923.debug.tools.common.protocal;

/**
 * @author future0923
 */
public interface Command {

    Byte HEARTBEAT_REQUEST = 1;

    Byte HEARTBEAT_RESPONSE = 2;

    Byte RUN_TARGET_METHOD_REQUEST = 3;

    Byte RUN_TARGET_METHOD_RESPONSE = 4;

    Byte SERVER_CLOSE_REQUEST = 5;

    Byte CLEAR_RUN_RESULT = 7;

    Byte RUN_GROOVY_SCRIPT_REQUEST = 8;

    Byte RUN_GROOVY_SCRIPT_RESPONSE = 9;

    Byte LOCAL_COMPILER_HOT_DEPLOY_REQUEST = 10;

    Byte REMOTE_COMPILER_HOT_DEPLOY_REQUEST = 11;

    Byte REMOTE_COMPILER_HOT_DEPLOY_RESPONSE = 12;
}
