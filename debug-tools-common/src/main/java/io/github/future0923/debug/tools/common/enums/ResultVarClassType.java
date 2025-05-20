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
package io.github.future0923.debug.tools.common.enums;

import lombok.Getter;

/**
 * @author future0923
 */
@Getter
public enum ResultVarClassType {

    INT("int"),
    OBJECT("object"),
    BOOLEAN("boolean"),
    BYTE("byte"),
    SHORT("short"),
    CHAR("char"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    COLLECTION("collection"),
    MAP("map"),
    MAP_ENTRY("map.entry"),
    ;
    private final String type;

    ResultVarClassType(String type) {
        this.type = type;
    }

    public static String getByClass(Class<?> clazz) {
        for (ResultVarClassType value : values()) {
            if (value.getType().equals(clazz.getName())) {
                return value.getType();
            }
        }
        return OBJECT.getType();
    }

    public static ResultVarClassType getByType(String type) {
        for (ResultVarClassType value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return OBJECT;
    }
}
