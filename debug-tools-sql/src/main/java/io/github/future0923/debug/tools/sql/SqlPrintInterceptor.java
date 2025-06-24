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
import io.github.future0923.debug.tools.base.logging.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * 打印SQL字节码拦截器
 *
 * @author future0923
 */
public class SqlPrintInterceptor {

    private static final Logger logger = Logger.getLogger(SqlPrintInterceptor.class);

    private static final String CONNECTION_AGENT_METHODS = "prepareStatement";

    private static final List<String> PREPARED_STATEMENT_METHODS = Arrays.asList("execute", "executeUpdate", "executeQuery", "addBatch");

    private static final String STATEMENT_PREFIXES = "com.mysql.jdbc.ClientPreparedStatement:";

    private static final String CJ_STATEMENT_PREFIXES = "com.mysql.cj.jdbc.ClientPreparedStatement:";

    private static PrintSqlType printSqlType;

    public static void setPrintSqlType(String printSqlType) {
        SqlPrintInterceptor.printSqlType = PrintSqlType.of(printSqlType);
        ;
    }

    public static Connection proxyConnection(final Connection connection) {
        Object c = Proxy.newProxyInstance(
                SqlPrintByteCodeEnhance.class.getClassLoader(),
                new Class[]{Connection.class},
                new ConnectionHandler(connection)
        );
        return (Connection) c;
    }


    private static PreparedStatement proxyPreparedStatement(final PreparedStatement statement) {
        Object c = Proxy.newProxyInstance(
                SqlPrintByteCodeEnhance.class.getClassLoader(),
                new Class[]{PreparedStatement.class},
                new PreparedStatementHandler(statement)
        );
        return (PreparedStatement) c;
    }

    /**
     * connection 代理处理
     */
    private static class ConnectionHandler implements InvocationHandler {

        private final Connection connection;

        private ConnectionHandler(Connection connection) {
            this.connection = connection;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(connection, args);
            if (CONNECTION_AGENT_METHODS.equals(method.getName())) {
                return proxyPreparedStatement((PreparedStatement) result);
            }
            return result;
        }
    }

    /**
     * PreparedStatement 代理处理
     */
    private static class PreparedStatementHandler implements InvocationHandler {

        private final PreparedStatement statement;

        public PreparedStatementHandler(PreparedStatement statement) {
            this.statement = statement;

        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long startTime = System.currentTimeMillis();
            Object result = method.invoke(statement, args);
            long endTime = System.currentTimeMillis();
            if (PREPARED_STATEMENT_METHODS.stream().anyMatch(s -> s.equals(method.getName()))) {
                printSql(endTime - startTime, statement);
            }
            return result;
        }
    }

    private static void printSql(long consume, Statement sta) {
        final String sql = sta.toString().replace("** BYTE ARRAY DATA **", "NULL");
        String resultSql;
        if (sql.startsWith(STATEMENT_PREFIXES)) {
            resultSql = sql.replace(STATEMENT_PREFIXES, "");
        } else if (sql.startsWith(CJ_STATEMENT_PREFIXES)) {
            resultSql = sql.replace(CJ_STATEMENT_PREFIXES, "");
        } else {
            resultSql = sql;
        }
        if (PrintSqlType.PRETTY.equals(printSqlType) || PrintSqlType.YES.equals(printSqlType)) {
            resultSql = SqlFormatter.format(resultSql);
        }
        if (PrintSqlType.COMPRESS.equals(printSqlType)) {
            resultSql = SqlCompressor.compressSql(resultSql);
        }
        logger.info("Execute consume Time: {} ms; Execute SQL: \n\u001B[31m{}\u001B[0m", consume, resultSql);
    }
}
