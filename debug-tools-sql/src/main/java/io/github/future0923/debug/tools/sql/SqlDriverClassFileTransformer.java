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
import io.github.future0923.debug.tools.hotswap.core.javassist.ByteArrayClassPath;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 转换驱动类字节码
 *
 * @author future0923
 */
public class SqlDriverClassFileTransformer implements ClassFileTransformer {

    private static final Logger logger = Logger.getLogger(SqlDriverClassFileTransformer.class);

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        try {
            String dotClassName = className.replace('/', '.');
            if (!isTargetDriver(dotClassName)) {
                return null;
            }
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ByteArrayClassPath(dotClassName, classfileBuffer));
            CtClass ctClass = classPool.get(dotClassName);
            CtMethod connectMethod = ctClass.getDeclaredMethod("connect", new CtClass[]{classPool.get("java.lang.String"), classPool.get("java.util.Properties")});
            connectMethod.insertAfter("{ " +
                    "   return " + SqlPrintInterceptor.class.getName() + ".proxyConnection((java.sql.Connection)$_); " +
            "}");
            logger.info("Print {} log bytecode enhancement successful", getSqlDriverType(dotClassName));
            return ctClass.toBytecode();
        } catch (Throwable t) {
            logger.error("Failed to print SQL log bytecode enhancement", t);
        }
        return null;
    }

    private boolean isTargetDriver(String className) {
        return className.equals("com.mysql.jdbc.NonRegisteringDriver")
                || className.equals("com.mysql.cj.jdbc.NonRegisteringDriver")
                || className.equals("org.postgresql.Driver")
                || className.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")
                || className.equals("ru.yandex.clickhouse.ClickHouseDriver")
                || className.equals("oracle.jdbc.OracleDriver");
    }

    private String getSqlDriverType(String className) {
        if (className.contains("mysql")) {
            return "mysql";
        }
        if (className.contains("postgresql")) {
            return "postgresql";
        }
        if (className.contains("sqlserver")) {
            return "sqlserver";
        }
        if (className.contains("clickhouse")) {
            return "clickhouse";
        }
        if (className.contains("oracle")) {
            return "oracle";
        }
        return "";
    }
}
