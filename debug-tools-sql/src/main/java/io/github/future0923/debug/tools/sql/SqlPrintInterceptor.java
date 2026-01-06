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
package io.github.future0923.debug.tools.sql;

import io.github.future0923.debug.tools.base.enums.PrintSqlType;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;
import io.github.future0923.debug.tools.base.hutool.sql.SqlCompressor;
import io.github.future0923.debug.tools.base.hutool.sql.SqlFormatter;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.trace.MethodTrace;
import io.github.future0923.debug.tools.utils.SqlFileWriter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 打印SQL字节码拦截器
 * @author future0923
 */
public class SqlPrintInterceptor {

    private static final Logger logger = Logger.getLogger(SqlPrintInterceptor.class);

    private static final List<String> CONNECTION_AGENT_METHODS = Arrays.asList("prepareStatement", "createStatement");

    private static final List<String> PREPARED_STATEMENT_METHODS = Arrays.asList("execute", "executeUpdate", "executeQuery", "addBatch");

    public static PrintSqlType printSqlType = PrintSqlType.NO;
    private static Boolean autoSaveSql = false;
    private static Integer sqlRetentionDays = 7;

    public static void setPrintSqlType(String printSqlType) {
        SqlPrintInterceptor.printSqlType = PrintSqlType.of(printSqlType);
    }

    public static void setAutoSaveSql(Boolean autoSave) {
        autoSaveSql = autoSave;
    }

    public static void setSqlRetentionDays(Integer days) {
        if (days != null) {
            sqlRetentionDays = days;
        }
    }

    public static Connection proxyConnection(final Connection connection) {
        Object c = Proxy.newProxyInstance(
                SqlPrintByteCodeEnhance.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler(connection)
        );
        return (Connection) c;
    }


    private static Statement proxyStatement(final Statement statement) {
        Object c = Proxy.newProxyInstance(
                SqlPrintByteCodeEnhance.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                new StatementHandler(statement)
        );
        return (Statement) c;
    }

    /**
     * connection 代理处理
     */
    private static class ConnectionHandler implements InvocationHandler {

        private final Connection connection;

        private ConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(connection, args);
            if (CONNECTION_AGENT_METHODS.contains(method.getName())) {
                return proxyStatement((Statement) result);
            }
            return result;
        }
    }

    /**
     * PreparedStatement 代理处理
     */
    private static class StatementHandler implements InvocationHandler {

        private final Statement statement;

        private final List<Object> parameters = new ArrayList<>();

        public StatementHandler(Statement statement) {
            this.statement = statement;

        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (!PrintSqlType.isPrint(printSqlType.getType())) {
                return method.invoke(statement, args);
            }
            long startTime = System.currentTimeMillis();
            Object result = method.invoke(statement, args);
            long endTime = System.currentTimeMillis();
            if (method.getName().startsWith("setNull")) {
                // 显式记录 NULL 值
                int index = (Integer) args[0];
                while (parameters.size() < index) parameters.add(null);
                parameters.set(index - 1, "NULL"); // 标记为 SQL NULL
            } else if (method.getName().startsWith("set") && args != null && args.length >= 2) {
                int index = (Integer) args[0];
                while (parameters.size() < index) parameters.add(null);
                parameters.set(index - 1, args[1]);
            }
            if (PREPARED_STATEMENT_METHODS.stream().anyMatch(s -> s.equals(method.getName()))) {
                printSql(endTime - startTime, statement, parameters.toArray(new Object[0]), method, args);
                parameters.clear();
            }
            return result;
        }
    }

    private static void printSql(long consume, Statement sta, Object[] parameters, Method method, Object[] args) {
        String className = sta.getClass().getName();
        DataSourceDriverClassEnum dbType = DataSourceDriverClassEnum.of(className);
        if (dbType == null) {
            logger.error("The current database driver is not yet supported. Driver class: {}", className);
            return;
        }
        try {
            String resultSql = ObjectUtil.isNull(args) ? dbType.getFormat().format(sta, parameters) : args[0].toString();
            resultSql = resultSql.endsWith(";") ? resultSql : resultSql + ";";

            if (BooleanUtil.isTrue(MethodTrace.getTraceSqlStatus())) {
                MethodTrace.enterSql(resultSql);
                MethodTrace.exit(consume);
            }
            if (PrintSqlType.PRETTY.equals(printSqlType) || PrintSqlType.YES.equals(printSqlType)) {
                resultSql = SqlFormatter.format(resultSql);
            }
            if (PrintSqlType.COMPRESS.equals(printSqlType)) {
                resultSql = SqlCompressor.compressSql(resultSql);
            }
            logger.infoForce("Execute consume Time: {} ms; Execute SQL: \n\u001B[31m{}\u001B[0m", consume, resultSql);

            // 根据配置写入SQL记录到文件
            if (BooleanUtil.isTrue(autoSaveSql)) {
                try {
                    SqlFileWriter.writeSqlRecordWithRetention(resultSql, consume, dbType.getType(), sqlRetentionDays);
                } catch (Exception e) {
                    logger.error("Failed to write SQL record to file", e);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to print SQL", e);
        }
    }
}
