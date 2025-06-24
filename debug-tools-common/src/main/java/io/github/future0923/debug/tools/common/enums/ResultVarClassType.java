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
