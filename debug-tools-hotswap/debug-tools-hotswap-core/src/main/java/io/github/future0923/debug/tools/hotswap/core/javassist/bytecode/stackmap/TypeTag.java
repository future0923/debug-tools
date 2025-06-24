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
package io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.stackmap;

import io.github.future0923.debug.tools.hotswap.core.javassist.bytecode.StackMapTable;

public interface TypeTag {
    String TOP_TYPE = "*top*";
    TypeData.BasicType TOP = new TypeData.BasicType(TOP_TYPE, StackMapTable.TOP, ' ');
    TypeData.BasicType INTEGER = new TypeData.BasicType("int", StackMapTable.INTEGER, 'I');
    TypeData.BasicType FLOAT = new TypeData.BasicType("float", StackMapTable.FLOAT, 'F');
    TypeData.BasicType DOUBLE = new TypeData.BasicType("double", StackMapTable.DOUBLE, 'D');
    TypeData.BasicType LONG = new TypeData.BasicType("long", StackMapTable.LONG, 'J');

    // and NULL, THIS, OBJECT, UNINIT
}
