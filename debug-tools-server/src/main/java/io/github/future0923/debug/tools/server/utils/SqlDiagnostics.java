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

import io.github.future0923.debug.tools.sql.SqlRecentBuffer;
import java.util.*;
import static io.github.future0923.debug.tools.server.utils.RuntimeDiagnostics.map;

/** 对已采集的有界 SQL 窗口分组，不执行 SQL，也不把窗口统计当作全量指标。 */
public final class SqlDiagnostics {
    private SqlDiagnostics() { }
    public static Map<String, Object> aggregate(List<SqlRecentBuffer.Record> records, long since, int limit, String sortBy) {
        if (since < 0 || limit < 1 || limit > 100 || !Arrays.asList("TOTAL", "MAX", "COUNT").contains(sortBy))
            throw new IllegalArgumentException("Invalid SQL aggregation bounds or sortBy");
        Map<String, Group> groups = new LinkedHashMap<>();
        int samples = 0;
        for (SqlRecentBuffer.Record record : records) {
            if (record.timestamp < since) continue;
            String normalized = normalize(record.sql);
            String key = record.dbType + "\u0000" + normalized;
            Group group = groups.computeIfAbsent(key, ignored -> new Group(normalized, record.dbType));
            group.count++; group.total += Math.max(0, record.consumeTimeMillis); group.max = Math.max(group.max, record.consumeTimeMillis);
            samples++;
        }
        List<Group> ordered = new ArrayList<>(groups.values());
        ordered.sort(Comparator.<Group>comparingLong(g -> "COUNT".equals(sortBy) ? g.count : "MAX".equals(sortBy) ? g.max : g.total).reversed().thenComparing(g -> g.sql));
        List<Map<String, Object>> result = new ArrayList<>();
        for (Group group : ordered.subList(0, Math.min(limit, ordered.size())))
            result.add(map("normalizedSql", group.sql, "dbType", group.dbType, "count", group.count, "totalMillis", group.total,
                "maxMillis", group.max, "averageMillis", (double) group.total / group.count));
        return map("groups", result, "totalGroups", groups.size(), "sampleCount", samples, "windowCapacity", 500,
            "scope", "RECENT_CAPTURED_WINDOW", "normalization", "LEXICAL_LITERALS_AND_COMMENTS", "notFullHistory", true);
    }
    /** 保留标识符大小写，消除注释与字面量；不声称 SQL AST 或跨方言语义等价。 */
    public static String normalize(String sql) {
        if (sql == null) return "";
        StringBuilder out = new StringBuilder();
        int i = 0;
        while (i < sql.length()) {
            char ch = sql.charAt(i);
            if (ch == '\'' ) {
                i++;
                while (i < sql.length()) {
                    char current = sql.charAt(i++);
                    if (current == '\\' && i < sql.length()) { i++; continue; }
                    if (current == '\'') { if (i < sql.length() && sql.charAt(i) == '\'') { i++; continue; } break; }
                }
                out.append('?');
            } else if (i + 1 < sql.length() && ch == '-' && sql.charAt(i + 1) == '-') {
                i += 2; while (i < sql.length() && sql.charAt(i) != '\n') i++; space(out);
            } else if (i + 1 < sql.length() && ch == '/' && sql.charAt(i + 1) == '*') {
                i += 2; while (i + 1 < sql.length() && !(sql.charAt(i) == '*' && sql.charAt(i + 1) == '/')) i++;
                i = Math.min(sql.length(), i + 2); space(out);
            } else if (ch == '"' || ch == '`' || ch == '[') {
                char close = ch == '[' ? ']' : ch; out.append(ch); i++;
                while (i < sql.length()) {
                    char current = sql.charAt(i++); out.append(current);
                    if (current == close) { if (i < sql.length() && sql.charAt(i) == close) { out.append(sql.charAt(i++)); continue; } break; }
                }
            } else if (Character.isDigit(ch) && (i == 0 || !(Character.isJavaIdentifierPart(sql.charAt(i - 1)) || sql.charAt(i - 1) == '?'))) {
                i++; while (i < sql.length() && (Character.isDigit(sql.charAt(i)) || sql.charAt(i) == '.')) i++;
                if (i < sql.length() && (sql.charAt(i) == 'e' || sql.charAt(i) == 'E')) {
                    i++; if (i < sql.length() && (sql.charAt(i) == '+' || sql.charAt(i) == '-')) i++;
                    while (i < sql.length() && Character.isDigit(sql.charAt(i))) i++;
                }
                out.append('?');
            } else if (Character.isWhitespace(ch)) { space(out); i++; }
            else { out.append(ch); i++; }
        }
        return out.toString().trim();
    }
    private static void space(StringBuilder text) { if (text.length() > 0 && text.charAt(text.length()-1) != ' ') text.append(' '); }
    private static final class Group {
        final String sql, dbType; long count, total, max;
        Group(String sql, String dbType) { this.sql = sql; this.dbType = dbType; }
    }
}
