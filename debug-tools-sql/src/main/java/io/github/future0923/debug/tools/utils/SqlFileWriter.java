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
package io.github.future0923.debug.tools.utils;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SQL文件写入工具类
 */
public class SqlFileWriter {

    private static final Logger logger = Logger.getLogger(SqlFileWriter.class);

    private static final String SQL_DIR = String.format(".idea/%s/sql", ProjectConstants.NAME);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * 写入SQL记录到文件，支持保留天数和0天清空逻辑
     */
    public static void writeSqlRecordWithRetention(String sql, long consumeTime, String dbType, Integer days) {
        lock.lock();
        try {
            LocalDateTime now = LocalDateTime.now();
            String dateStr = now.format(DATE_FORMATTER);
            String timeStr = now.format(TIME_FORMATTER);

            String content = String.format(
                    "-- %s | %s | %dms\n%s;\n\n",
                    timeStr, dbType, consumeTime, sql
            );

            String projectPath = System.getProperty("user.dir");
            Path sqlDir = Paths.get(projectPath, SQL_DIR);
            if (!Files.exists(sqlDir)) {
                Files.createDirectories(sqlDir);
            }

            // 删除超出天数的文件或全部删除
            if (days == 0) {
                // 删除所有sql文件
                Files.list(sqlDir)
                        .filter(p -> p.getFileName().toString().endsWith(".sql"))
                        .forEach(p -> {
                            try { Files.deleteIfExists(p); } catch (IOException ignore) {}
                        });
            } else if (days > 0) {
                LocalDateTime threshold = now.minusDays(days - 1); // 保留N天，含今天
                Files.list(sqlDir)
                        .filter(p -> p.getFileName().toString().endsWith(".sql"))
                        .forEach(p -> {
                            String name = p.getFileName().toString();
                            String date = name.replace(".sql", "");
                            try {
                                LocalDateTime fileDate = LocalDateTime.parse(date + "T00:00:00");
                                if (fileDate.isBefore(threshold.withHour(0).withMinute(0).withSecond(0).withNano(0))) {
                                    Files.deleteIfExists(p);
                                }
                            } catch (Exception ignore) {}
                        });
            }

            Path sqlFile = sqlDir.resolve(dateStr + ".sql");
            if (days == 0) {
                // 0天：每次都覆盖写入（清空）
                Files.write(
                        sqlFile,
                        content.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            } else {
                // 正常追加
                Files.write(
                        sqlFile,
                        content.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            }
            logger.debug("SQL record written to file: {}", sqlFile);
        } catch (IOException e) {
            logger.error("Failed to write SQL record to file", e);
        } finally {
            lock.unlock();
        }
    }
}
