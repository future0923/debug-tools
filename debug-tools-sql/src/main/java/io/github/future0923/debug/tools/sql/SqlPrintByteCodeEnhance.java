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

import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.hutool.core.util.BooleanUtil;

import java.lang.instrument.Instrumentation;

/**
 * 字节码增强打印SQL
 *
 * @author future0923
 */
public class SqlPrintByteCodeEnhance {

    /**
     * 增加字节码让其打印SQL
     *
     * @param inst     instrumentation
     */
    public static void enhance(Instrumentation inst, AgentArgs agentArgs) {
        SqlPrintInterceptor.setPrintSqlType(agentArgs.getPrintSql());
        SqlPrintInterceptor.setAutoSaveSql(BooleanUtil.toBoolean(agentArgs.getAutoSaveSql()));
        SqlPrintInterceptor.setSqlRetentionDays(agentArgs.getSqlRetentionDays());
        inst.addTransformer(new SqlDriverClassFileTransformer(), true);
    }
}
