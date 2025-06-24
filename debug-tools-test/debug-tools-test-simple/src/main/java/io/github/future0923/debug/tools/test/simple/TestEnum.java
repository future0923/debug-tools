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
package io.github.future0923.debug.tools.test.simple;

/**
 * @author future0923
 */
public enum TestEnum {

    A(11, "A1"),
    B(21, "B1"),
    C(3, "C"),
    ;

    private final Integer code;

    private final String name;

    TestEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Object getByCode() {
        return values();
    }

    @Override
    public String toString() {
        return "TestEnum{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}
