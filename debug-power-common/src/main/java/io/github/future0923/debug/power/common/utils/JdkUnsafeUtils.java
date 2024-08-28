package io.github.future0923.debug.power.common.utils;

import io.github.future0923.debug.power.common.enums.ResultVarClassType;
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
