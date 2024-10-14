package io.github.future0923.debug.tools.attach.sqlprint;

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
