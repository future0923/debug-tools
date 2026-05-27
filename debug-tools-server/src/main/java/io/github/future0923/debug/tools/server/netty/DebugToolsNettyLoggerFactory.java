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
import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Keeps shaded Netty startup noise away from the target application's logging system.
 */
final class DebugToolsNettyLoggerFactory extends InternalLoggerFactory {

    static final DebugToolsNettyLoggerFactory INSTANCE = new DebugToolsNettyLoggerFactory();

    private static final Logger LOGGER = Logger.getLogger(DebugToolsNettyLoggerFactory.class);

    private DebugToolsNettyLoggerFactory() {
    }

    static void install() {
        InternalLoggerFactory.setDefaultFactory(INSTANCE);
    }

    @Override
    public InternalLogger newInstance(String name) {
        return new DebugToolsNettyLogger(name);
    }

    private static final class DebugToolsNettyLogger extends AbstractInternalLogger {

        private static final long serialVersionUID = 1L;

        private DebugToolsNettyLogger(String name) {
            super(name);
        }

        @Override
        public boolean isTraceEnabled() {
            return false;
        }

        @Override
        public void trace(String msg) {
        }

        @Override
        public void trace(String format, Object arg) {
        }

        @Override
        public void trace(String format, Object argA, Object argB) {
        }

        @Override
        public void trace(String format, Object... arguments) {
        }

        @Override
        public void trace(String msg, Throwable t) {
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void debug(String msg) {
        }

        @Override
        public void debug(String format, Object arg) {
        }

        @Override
        public void debug(String format, Object argA, Object argB) {
        }

        @Override
        public void debug(String format, Object... arguments) {
        }

        @Override
        public void debug(String msg, Throwable t) {
        }

        @Override
        public boolean isInfoEnabled() {
            return false;
        }

        @Override
        public void info(String msg) {
        }

        @Override
        public void info(String format, Object arg) {
        }

        @Override
        public void info(String format, Object argA, Object argB) {
        }

        @Override
        public void info(String format, Object... arguments) {
        }

        @Override
        public void info(String msg, Throwable t) {
        }

        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        @Override
        public void warn(String msg) {
            LOGGER.warning("Netty: {}", msg);
        }

        @Override
        public void warn(String format, Object arg) {
            LOGGER.warning("Netty: " + format, arg);
        }

        @Override
        public void warn(String format, Object argA, Object argB) {
            LOGGER.warning("Netty: " + format, argA, argB);
        }

        @Override
        public void warn(String format, Object... arguments) {
            LOGGER.warning("Netty: " + format, arguments);
        }

        @Override
        public void warn(String msg, Throwable t) {
            LOGGER.warning("Netty: {}", t, msg);
        }

        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        @Override
        public void error(String msg) {
            LOGGER.error("Netty: {}", msg);
        }

        @Override
        public void error(String format, Object arg) {
            LOGGER.error("Netty: " + format, arg);
        }

        @Override
        public void error(String format, Object argA, Object argB) {
            LOGGER.error("Netty: " + format, argA, argB);
        }

        @Override
        public void error(String format, Object... arguments) {
            LOGGER.error("Netty: " + format, arguments);
        }

        @Override
        public void error(String msg, Throwable t) {
            LOGGER.error("Netty: {}", t, msg);
        }

        @Override
        public boolean isEnabled(InternalLogLevel level) {
            return level == InternalLogLevel.WARN || level == InternalLogLevel.ERROR;
        }
    }
}
