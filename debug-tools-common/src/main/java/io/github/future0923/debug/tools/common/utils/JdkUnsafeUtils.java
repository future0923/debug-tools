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
package io.github.future0923.debug.tools.common.utils;

import io.github.future0923.debug.tools.common.enums.ResultVarClassType;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author future0923
 */
public class JdkUnsafeUtils {

    private static final Unsafe unsafe;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (Unsafe) field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static long getObjectFieldOffset(Field field) {
        return unsafe.objectFieldOffset(field);
    }

    public static Object getObject(Object object, long offset, String type) {
        if (ResultVarClassType.INT.getType().equals(type)) {
            return unsafe.getInt(object, offset);
        }
        if (ResultVarClassType.OBJECT.getType().equals(type)) {
            return unsafe.getObject(object, offset);
        }
        if (ResultVarClassType.BOOLEAN.getType().equals(type)) {
            return unsafe.getBoolean(object, offset);
        }
        if (ResultVarClassType.BYTE.getType().equals(type)) {
            return unsafe.getByte(object, offset);
        }
        if (ResultVarClassType.SHORT.getType().equals(type)) {
            return unsafe.getShort(object, offset);
        }
        if (ResultVarClassType.CHAR.getType().equals(type)) {
            return unsafe.getChar(object, offset);
        }
        if (ResultVarClassType.LONG.getType().equals(type)) {
            return unsafe.getLong(object, offset);
        }
        if (ResultVarClassType.FLOAT.getType().equals(type)) {
            return unsafe.getFloat(object, offset);
        }
        if (ResultVarClassType.DOUBLE.getType().equals(type)) {
            return unsafe.getDouble(object, offset);
        }
        return unsafe.getObject(object, offset);
    }
}
