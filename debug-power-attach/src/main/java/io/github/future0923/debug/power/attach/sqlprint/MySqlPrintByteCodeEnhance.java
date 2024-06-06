package io.github.future0923.debug.power.attach.sqlprint;

import io.github.future0923.debug.power.base.logging.Logger;
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
 * @author future0923
 */
public class MySqlPrintByteCodeEnhance {

    private static final Logger logger = Logger.getLogger(MySqlPrintByteCodeEnhance.class);

    public static void enhance(Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.named("com.mysql.jdbc.NonRegisteringDriver")
                        .or(ElementMatchers.named("com.mysql.cj.jdbc.NonRegisteringDriver")))
                .transform(new Transformer())
                .with(new AgentBuilder.Listener.Adapter() {
                    @Override
                    public void onTransformation(TypeDescription typeDescription, @MaybeNull ClassLoader classLoader, @MaybeNull JavaModule module, boolean loaded, DynamicType dynamicType) {
                        logger.info("Print mysql log bytecode enhancement successful");
                    }

                    @Override
                    public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
                        logger.error("Failed to print mysql log bytecode enhancement", throwable);
                    }
                }).installOn(inst);
    }
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
