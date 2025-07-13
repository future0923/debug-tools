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
     * 写入SQL记录到文件(紧凑版)
     */
    public static void writeSqlRecord(String sql, long consumeTime, String dbType) {
        lock.lock();
        try {
            LocalDateTime now = LocalDateTime.now();
            String dateStr = now.format(DATE_FORMATTER);
            String timeStr = now.format(TIME_FORMATTER);

            // 紧凑化的SQL记录格式
            String content = String.format(
                    "-- %s | %s | %dms\n%s;\n\n",
                    timeStr, dbType, consumeTime, sql
            );

            String projectPath = System.getProperty("user.dir");
            Path sqlDir = Paths.get(projectPath, SQL_DIR);
            if (!Files.exists(sqlDir)) {
                Files.createDirectories(sqlDir);
            }

            Path sqlFile = sqlDir.resolve(dateStr + ".sql");

            Files.write(
                    sqlFile,
                    content.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            logger.debug("SQL record written to file: {}", sqlFile);

        } catch (IOException e) {
            logger.error("Failed to write SQL record to file", e);
        } finally {
            lock.unlock();
        }
    }

}
