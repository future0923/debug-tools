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
package io.github.future0923.debug.tools.base.context;

import io.github.future0923.debug.tools.base.tuple.Tuple2;

/**
 * @author future0923
 */
public class RunMethodContext {

    /**
     * 运行方法上下文
     * Tuple2<className, methodName>
     */
    private static final ThreadLocal<Tuple2<String, String>> RUN_METHOD_CONTEXT = new ThreadLocal<>();

    public static void setRunMethod(String className, String methodName) {
        RUN_METHOD_CONTEXT.set(Tuple2.of(className, methodName));
    }

    public static Tuple2<String, String> getRunMethod() {
        return RUN_METHOD_CONTEXT.get();
    }

    public static void clear() {
        RUN_METHOD_CONTEXT.remove();
    }
}
