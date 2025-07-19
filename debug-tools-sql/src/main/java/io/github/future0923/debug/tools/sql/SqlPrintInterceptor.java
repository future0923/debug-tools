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
import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
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
    private static boolean autoSaveSql = false;
    private static int sqlRetentionDays = 7;

    public static void setPrintSqlType(String printSqlType) {
        SqlPrintInterceptor.printSqlType = PrintSqlType.of(printSqlType);
    }

    public static void setAutoSaveSql(boolean autoSave) {
        autoSaveSql = autoSave;
    }

    public static void setSqlRetentionDays(int days) {
        sqlRetentionDays = days;
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

        @Override
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

        private final List<Object> parameters = new ArrayList<>();

        public PreparedStatementHandler(PreparedStatement statement) {
            this.statement = statement;

        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long startTime = System.currentTimeMillis();
            Object result = method.invoke(statement, args);
            long endTime = System.currentTimeMillis();
            if (method.getName().startsWith("set") && args != null && args.length >= 2) {
                int index = (Integer) args[0];
                while (parameters.size() < index) parameters.add(null);
                parameters.set(index - 1, args[1]);
            }
            if (PREPARED_STATEMENT_METHODS.stream().anyMatch(s -> s.equals(method.getName()))) {
                printSql(endTime - startTime, statement, parameters.toArray(new Object[0]));
                parameters.clear();
            }
            return result;
        }
    }

    private static void printSql(long consume, Statement sta, Object[] parameters) {
        String resultSql;
        String className = sta.getClass().getName();
        String dbType = getDbType(className);
        
        if ("SQLServer".equals(dbType)) {
            resultSql = printSQLServer(sta);
        } else if ("MySQL".equals(dbType)) {
            resultSql = printMySQL(sta);
        } else if ("PostgreSQL".equals(dbType)) {
            resultSql = printPostgresql(sta);
        } else if ("Oracle".equals(dbType)) {
            resultSql = printOracle(sta, parameters);
        } else {
            resultSql = sta.toString();
        }
        if (PrintSqlType.PRETTY.equals(printSqlType) || PrintSqlType.YES.equals(printSqlType)) {
            resultSql = SqlFormatter.format(resultSql);
        }
        if (PrintSqlType.COMPRESS.equals(printSqlType)) {
            resultSql = SqlCompressor.compressSql(resultSql);
        }
        logger.info("Execute consume Time: {} ms; Execute SQL: \n\u001B[31m{}\u001B[0m", consume, resultSql);
        
        // 根据配置写入SQL记录到文件
        if (autoSaveSql) {
            try {
                SqlFileWriter.writeSqlRecordWithRetention(resultSql, consume, dbType, sqlRetentionDays);
            } catch (Exception e) {
                logger.error("Failed to write SQL record to file", e);
            }
        }
    }
    
    /**
     * 获取数据库类型
     */
    private static String getDbType(String className) {
        if (className.startsWith("com.microsoft.sqlserver")) {
            return "SQLServer";
        } else if (className.startsWith("com.mysql")) {
            return "MySQL";
        } else if (className.startsWith("org.postgresql")) {
            return "PostgreSQL";
        } else if (className.startsWith("oracle.jdbc")) {
            return "Oracle";
        } else {
            return "Unknown";
        }
    }

    private static String printPostgresql(Statement sta) {
        return sta.toString();
    }

    private static String printMySQL(Statement sta) {
        final String sql = sta.toString().replace("** BYTE ARRAY DATA **", "NULL");
        String resultSql;
        if (sql.startsWith(STATEMENT_PREFIXES)) {
            resultSql = sql.replace(STATEMENT_PREFIXES, "");
        } else if (sql.startsWith(CJ_STATEMENT_PREFIXES)) {
            resultSql = sql.replace(CJ_STATEMENT_PREFIXES, "");
        } else {
            resultSql = sql;
        }
        return resultSql;
    }

    private static String printSQLServer(Statement sta) {
        Object[] inOutParam = (Object[]) ReflectUtil.getFieldValue(sta, "inOutParam");
        Object[] parameterValues = new Object[inOutParam.length];
        for (int i = 0; i < inOutParam.length; i++) {
            parameterValues[i] = ReflectUtil.invoke(inOutParam[i], "getSetterValue");
        }
        final String statementQuery = (String) ReflectUtil.getFieldValue(sta, "userSQL");
        return formatStringSql(statementQuery, parameterValues);
    }

    private static String printOracle(Statement sta, Object[] parameters) {
        String statementQuery = ReflectUtil.getFieldValue(ReflectUtil.getFieldValue(sta, "preparedStatement"), "sqlObject").toString();
        return formatStringSql(statementQuery, parameters);
    }

    private static String formatStringSql(String statementQuery, Object[] parameterValues) {
        final StringBuilder sb = new StringBuilder();
        int currentParameter = 0;
        for( int pos = 0; pos < statementQuery.length(); pos ++) {
            char character = statementQuery.charAt(pos);
            if( statementQuery.charAt(pos) == '?' && currentParameter <= parameterValues.length) {
                Object getSetterValue = parameterValues[currentParameter];
                if (getSetterValue instanceof String) {
                    sb.append("'").append(getSetterValue).append("'");
                } else {
                    sb.append(Convert.toStr(getSetterValue));
                }
                currentParameter++;
            } else {
                sb.append(character);
            }
        }
        return sb.toString();
    }
}
