package io.github.future0923.debug.power.common.utils;

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

    public static Object getObject(Object object, long offset) {
        return unsafe.getObject(object, offset);
    }

    public static void main(String[] args) {
        System.out.println(unsafe.getObject(new Object(), 0));
    }
}
