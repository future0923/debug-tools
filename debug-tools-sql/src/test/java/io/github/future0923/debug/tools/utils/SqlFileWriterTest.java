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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlFileWriterTest {

    @TempDir
    Path homeDir;

    @Test
    void writesSqlRecordUnderApplicationAndDateFile() throws Exception {
        String oldUserHome = System.getProperty("user.home");
        System.setProperty("user.home", homeDir.toString());
        try {
            SqlFileWriter.writeSqlRecord("select 1", 12, "mysql", "order service");

            Path sqlFile = homeDir
                    .resolve(".debugTools/sql/order-service")
                    .resolve(LocalDate.now() + ".sql");
            assertTrue(Files.exists(sqlFile));
            assertTrue(new String(Files.readAllBytes(sqlFile)).contains("select 1;"));
        } finally {
            System.setProperty("user.home", oldUserHome);
        }
    }

    @Test
    void writesSqlRecordUnderProjectAndMainClassWhenProjectContextExists() throws Exception {
        String oldUserHome = System.getProperty("user.home");
        System.setProperty("user.home", homeDir.toString());
        try {
            SqlFileWriter.writeSqlRecord(
                    "select 1",
                    12,
                    "mysql",
                    "com.foo.OrderApplication",
                    "order service",
                    "a8f31c2b"
            );

            Path sqlFile = homeDir
                    .resolve(".debugTools/sql/order-service-a8f31c2b/com.foo.OrderApplication")
                    .resolve(LocalDate.now() + ".sql");
            assertTrue(Files.exists(sqlFile));
            assertTrue(new String(Files.readAllBytes(sqlFile)).contains("select 1;"));
        } finally {
            System.setProperty("user.home", oldUserHome);
        }
    }

    @Test
    void doesNotDeleteOlderSqlFilesAutomatically() throws Exception {
        String oldUserHome = System.getProperty("user.home");
        System.setProperty("user.home", homeDir.toString());
        try {
            Path oldSql = homeDir.resolve(".debugTools/sql/order-service/2026-05-01.sql");
            Files.createDirectories(oldSql.getParent());
            Files.write(oldSql, "select old;".getBytes());

            SqlFileWriter.writeSqlRecord("select 1", 12, "mysql", "order-service");

            assertTrue(Files.exists(oldSql));
        } finally {
            System.setProperty("user.home", oldUserHome);
        }
    }

    @Test
    void doesNotWriteSqlToFlatHistoryRoot() throws Exception {
        String oldUserHome = System.getProperty("user.home");
        System.setProperty("user.home", homeDir.toString());
        try {
            SqlFileWriter.writeSqlRecord("select 1", 12, "mysql", "order-service");

            assertFalse(Files.exists(homeDir.resolve(".debugTools/sql/" + LocalDate.now() + ".sql")));
        } finally {
            System.setProperty("user.home", oldUserHome);
        }
    }
}
