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
package io.github.future0923.debug.tools.server.utils;

import io.github.future0923.debug.tools.base.logging.DebugToolsLogBuffer;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/** 使用真实框架事件验证注册、来源、重复注册和停止后的解绑。 */
class BusinessLogCaptureTest {
    @Test void logbackCapturesFormattedBusinessEventsAndDetaches() throws Exception {
        String marker = "logback-" + UUID.randomUUID();
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("debugtools.capture.test");
        try {
            Map<String, Object> first = BusinessLogCapture.start("logback-loader", getClass().getClassLoader(), "LOGBACK");
            assertEquals(true, first.get("active"));
            BusinessLogCapture.start("logback-loader", getClass().getClassLoader(), "LOGBACK");
            logger.error("{} {}", marker, "business");
            List<DebugToolsLogBuffer.Record> records = DebugToolsLogBuffer.recent(200, 0, null, marker);
            assertEquals(1, records.size());
            assertEquals("LOGBACK", records.get(0).source);
            assertEquals(marker + " business", records.get(0).message);
            BusinessLogCapture.stop("logback-loader", "LOGBACK");
            logger.error("{} stopped", marker);
            assertEquals(1, DebugToolsLogBuffer.recent(200, 0, null, marker).size());
        } finally { BusinessLogCapture.closeAll(); }
    }

    @Test void log4jCapturesEventsAndKeepsExistingLoggingConfiguration() throws Exception {
        String marker = "log4j-" + UUID.randomUUID();
        org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("debugtools.capture.test");
        Object level = logger.getLevel();
        try {
            assertEquals(true, BusinessLogCapture.start("log4j-loader", getClass().getClassLoader(), "LOG4J2").get("active"));
            logger.error("{} {}", marker, "business");
            List<DebugToolsLogBuffer.Record> records = DebugToolsLogBuffer.recent(200, 0, null, marker);
            assertEquals(1, records.size());
            assertEquals("LOG4J2", records.get(0).source);
            assertEquals(level, logger.getLevel());
            BusinessLogCapture.stop("log4j-loader", "LOG4J2");
            logger.error("{} stopped", marker);
            assertEquals(1, DebugToolsLogBuffer.recent(200, 0, null, marker).size());
        } finally { BusinessLogCapture.closeAll(); }
    }

    @Test void oversizedLogsRemainBoundedAndKeepSource() {
        DebugToolsLogBuffer.add(1234L, "LOGBACK", "ERROR", "test", "thread", String.join("", Collections.nCopies(20000, "x")), String.join("", Collections.nCopies(20000, "y")));
        DebugToolsLogBuffer.Record record = DebugToolsLogBuffer.recent(1, 0, null, null).get(0);
        assertEquals(16384, record.message.length());
        assertEquals(16384, record.throwable.length());
        assertEquals(1234L, record.timestamp);
    }
}
