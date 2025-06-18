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
     * @param printSql 打印类型
     */
    public static void enhance(Instrumentation inst, String printSql) {
        SqlPrintInterceptor.setPrintSqlType(printSql);
        inst.addTransformer(new SqlDriverClassFileTransformer(), true);
    }
}
