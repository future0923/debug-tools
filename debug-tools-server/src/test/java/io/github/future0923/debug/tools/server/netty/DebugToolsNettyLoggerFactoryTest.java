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
package io.github.future0923.debug.tools.server.netty;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.logging.LoggerHandler;
import io.netty.util.internal.logging.InternalLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebugToolsNettyLoggerFactoryTest {

    private final LoggerHandler originalHandler = Logger.getHandler();
    private final CapturingLoggerHandler capturingHandler = new CapturingLoggerHandler();

    @AfterEach
    void tearDown() {
        Logger.setHandler(originalHandler);
    }

    @Test
    void disablesNettyDebugAndInfoLogs() {
        Logger.setHandler(capturingHandler);
        InternalLogger logger = DebugToolsNettyLoggerFactory.INSTANCE.newInstance("netty-test");

        logger.debug("debug {}", "noise");
        logger.info("info {}", "noise");

        assertFalse(logger.isDebugEnabled());
        assertFalse(logger.isInfoEnabled());
        assertTrue(capturingHandler.messages.isEmpty());
    }

    @Test
    void forwardsNettyWarnAndErrorLogsToDebugToolsLogger() {
        Logger.setHandler(capturingHandler);
        InternalLogger logger = DebugToolsNettyLoggerFactory.INSTANCE.newInstance("netty-test");

        logger.warn("warn {}", "kept");
        logger.error("error {}", "kept");

        assertTrue(logger.isWarnEnabled());
        assertTrue(logger.isErrorEnabled());
        assertTrue(capturingHandler.messages.stream().anyMatch(message -> message.contains("warn kept")));
        assertTrue(capturingHandler.messages.stream().anyMatch(message -> message.contains("error kept")));
    }

    private static class CapturingLoggerHandler extends LoggerHandler {

        private final List<String> messages = new ArrayList<>();

        @Override
        protected void printMessage(String message) {
            messages.add(message);
        }
    }
}
