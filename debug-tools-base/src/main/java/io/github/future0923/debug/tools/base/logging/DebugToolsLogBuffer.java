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
package io.github.future0923.debug.tools.base.logging;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Bounded in-process log buffer used by the DebugTools HTTP diagnostics API. */
public final class DebugToolsLogBuffer {
    private static final int MAX_ENTRIES = 500;
    private static final int MAX_BYTES = 512 * 1024;
    private static final Deque<Record> RECORDS = new ArrayDeque<>();
    private static int bytes;

    private DebugToolsLogBuffer() { }

    public static synchronized void add(String level, String logger, String thread, String message, String throwable) {
        add(System.currentTimeMillis(), "DEBUG_TOOLS", level, logger, thread, message, throwable);
    }

    /** 业务日志与 agent 日志保留来源和原时间戳，所有文本均计入缓冲预算。 */
    public static synchronized void add(long timestamp, String source, String level, String logger, String thread, String message, String throwable) {
        add(timestamp, source, level, logger, thread, message, throwable, io.github.future0923.debug.tools.base.trace.InvocationContext.currentId());
    }

    /** 异步日志框架可传入事件创建时冻结的关联 ID。 */
    public static synchronized void add(long timestamp, String source, String level, String logger, String thread, String message, String throwable, String invocationId) {
        Record record = new Record(timestamp, cut(level, 32), cut(logger, 1024), cut(thread, 1024), cut(message, 16384), cut(throwable, 16384), cut(source, 64), cut(invocationId,128));
        RECORDS.addLast(record);
        bytes += size(record);
        while (RECORDS.size() > MAX_ENTRIES || bytes > MAX_BYTES) {
            Record removed = RECORDS.removeFirst();
            bytes -= size(removed);
        }
    }

    private static String cut(String value, int max) { return value == null ? "" : value.substring(0, Math.min(value.length(), max)); }
    private static int size(Record value) { return 2 * (value.level.length() + value.logger.length() + value.thread.length() + value.message.length() + value.throwable.length() + value.source.length() + value.invocationId.length()); }

    public static synchronized List<Record> recent(int limit, long since, String level, String keyword) {
        return recent(limit,since,level,keyword,null);
    }
    public static synchronized List<Record> recent(int limit, long since, String level, String keyword, String invocationId) {
        List<Record> result = new ArrayList<>();
        int max = Math.max(1, Math.min(limit, 200));
        for (Record record : RECORDS) {
            if (record.timestamp < since) continue;
            if (invocationId != null && !invocationId.equals(record.invocationId)) continue;
            if (level != null && !level.isEmpty() && !level.equalsIgnoreCase(record.level)) continue;
            if (keyword != null && !keyword.isEmpty() && !record.message.contains(keyword)) continue;
            result.add(record);
        }
        if (result.size() > max) return result.subList(result.size() - max, result.size());
        return result;
    }

    public static final class Record {
        public final long timestamp;
        public final String level;
        public final String logger;
        public final String thread;
        public final String message;
        public final String throwable;
        public final String source;
        public final String invocationId;
        public Record(long timestamp, String level, String logger, String thread, String message, String throwable) {
            this(timestamp, level, logger, thread, message, throwable, "DEBUG_TOOLS");
        }
        public Record(long timestamp, String level, String logger, String thread, String message, String throwable, String source) {
            this(timestamp,level,logger,thread,message,throwable,source,"");
        }
        public Record(long timestamp, String level, String logger, String thread, String message, String throwable, String source, String invocationId) {
            this.invocationId=invocationId;
            this.timestamp = timestamp; this.level = level; this.logger = logger; this.thread = thread; this.message = message; this.throwable = throwable; this.source = source;
        }
    }
}
