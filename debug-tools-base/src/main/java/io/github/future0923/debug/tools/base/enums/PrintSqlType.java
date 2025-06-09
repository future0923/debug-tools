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
package io.github.future0923.debug.tools.base.enums;

import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum PrintSqlType {

    /**
     * 格式化打印
     */
    PRETTY("Pretty"),

    /**
     * 压缩打印
     */
    COMPRESS("Compress"),

    /**
     * 不打印
     */
    NO("No"),

    /**
     * 兼容之前版本
     */
    YES("true"),
    ;

    private final String type;

    PrintSqlType(String type) {
        this.type = type;
    }

    /**
     * 是否开启打印
     */
    public static boolean isPrint(String type) {
        if (StrUtil.isBlank(type)) {
            return false;
        }
        return PRETTY.getType().equalsIgnoreCase(type)
                || COMPRESS.getType().equalsIgnoreCase(type)
                || YES.getType().equalsIgnoreCase(type);
    }

    public static PrintSqlType of(String type) {
        if (StrUtil.isBlank(type)) {
            return NO;
        }
        if (PRETTY.getType().equalsIgnoreCase(type) || YES.getType().equalsIgnoreCase(type)) {
            return PRETTY;
        }
        if (COMPRESS.getType().equalsIgnoreCase(type)) {
            return COMPRESS;
        }
        return NO;
    }
}
