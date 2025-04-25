package io.github.future0923.debug.tools.sql;

import io.github.future0923.debug.tools.base.logging.Logger;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import net.bytebuddy.utility.nullability.MaybeNull;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.util.Properties;

/**
 * 字节码增强打印SQL
 *
 * @author future0923
 */
public class SqlPrintByteCodeEnhance {

    private static final Logger logger = Logger.getLogger(SqlPrintByteCodeEnhance.class);

    /**
     * 增加字节码让其打印SQL
     *
     * @param inst instrumentation
     */
    public static void enhance(Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.named("com.mysql.jdbc.NonRegisteringDriver")
                        .or(ElementMatchers.named("com.mysql.cj.jdbc.NonRegisteringDriver"))
                        .or(ElementMatchers.named("org.postgresql.Driver"))
                        .or(ElementMatchers.named("com.microsoft.sqlserver.jdbc.SQLServerDriver"))
                        .or(ElementMatchers.named("ru.yandex.clickhouse.ClickHouseDriver"))
                        .or(ElementMatchers.named("oracle.jdbc.OracleDriver"))
                )
                .transform(new Transformer())
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                        logger.info("Print {} log bytecode enhancement successful", getSqlDriverType(typeDescription.getTypeName()));
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        logger.error("Failed to print {} log bytecode enhancement", throwable, getSqlDriverType(typeName));
                    }

                    private String getSqlDriverType(String typeName) {
                        String type = "";
                        if (typeName.contains("mysql")) {
                            type = "mysql";
                        } else if (typeName.contains("postgresql")) {
                            type = "postgresql";
                        } else if (typeName.contains("sqlserver")) {
                            type = "sqlserver";
                        } else if (typeName.contains("clickhouse")) {
                            type = "clickhouse";
                        } else if (typeName.contains("oracle")) {
                            type = "oracle";
                        }
                        return type;
                    }
                }).installOn(inst);
    }

    /**
     * 拦截connect方法，SqlPrintInterceptor打印SQL
     */
    public static class Transformer implements AgentBuilder.Transformer {

        @Override
        public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, ProtectionDomain protectionDomain) {
            return builder.method(ElementMatchers.named("connect")
                            .and(ElementMatchers.takesArgument(0, String.class))
                            .and(ElementMatchers.takesArgument(1, Properties.class))
                            .and(ElementMatchers.returns(Connection.class))
                    ) // 匹配所有方法
                    .intercept(MethodDelegation.to(SqlPrintInterceptor.class));
        }
    }
}
