/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.sql;

import io.github.future0923.debug.tools.base.logging.Logger;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

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

    @RuntimeType
    public static Object intercept(
            @SuperCall Callable<?> callable
    ) {
        Object result = null;
        try {
            result = callable.call();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            result = proxyConnection((Connection) result);
        }
        return result;
    }

    private static Connection proxyConnection(final Connection connection) {
        Object c = Proxy.newProxyInstance(SqlPrintByteCodeEnhance.class.getClassLoader()
                , new Class[]{Connection.class}, new ConnectionHandler(connection));
        return (Connection) c;
    }


    private static PreparedStatement proxyPreparedStatement(final PreparedStatement statement) {
        Object c = Proxy.newProxyInstance(SqlPrintByteCodeEnhance.class.getClassLoader()
                , new Class[]{PreparedStatement.class}, new PreparedStatementHandler(statement));
        return (PreparedStatement) c;
    }

    /**
     * connection 代理处理
     */
    public static class ConnectionHandler implements InvocationHandler {
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
    public static class PreparedStatementHandler implements InvocationHandler {
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

    public static void printSql(long consume, Statement sta) {
        final String sql = sta.toString().replace("** BYTE ARRAY DATA **", "NULL");
        String resultSql;
        if (sql.startsWith(STATEMENT_PREFIXES)) {
            resultSql = sql.replace(STATEMENT_PREFIXES, "");
        } else if (sql.startsWith(CJ_STATEMENT_PREFIXES)){
            resultSql = sql.replace(CJ_STATEMENT_PREFIXES, "");
        } else {
            resultSql = sql;
        }
        logger.info("Execute consume Time: {} ms; Execute SQL: \n\u001B[31m{}\u001B[0m", consume, SqlFormatter.format(resultSql));
    }
}
