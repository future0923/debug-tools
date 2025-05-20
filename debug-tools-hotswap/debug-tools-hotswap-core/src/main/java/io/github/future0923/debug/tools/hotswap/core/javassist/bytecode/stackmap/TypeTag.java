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
