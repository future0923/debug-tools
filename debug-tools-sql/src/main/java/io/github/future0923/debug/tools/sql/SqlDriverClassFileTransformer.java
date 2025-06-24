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
            if (className == null) {
                return null;
            }
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
