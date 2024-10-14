package io.github.future0923.debug.tools.test.application.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author future0923
 */
public class TestUtils {

    public static void test() {
        System.out.println("testStatic");
    }

    private static void testPrivate() {
        System.out.println("testPrivateStatic");
    }

    public static class TestInner {

        private static void testInnerPrivate() {
            System.out.println("testInnerPrivate");
        }
    }

    public static String LocalDateTimeToMMdd(LocalDateTime localDateTime) {
        DateTimeFormatter fmt24 = DateTimeFormatter.ofPattern("MM-dd");
        return localDateTime.format(fmt24);
    }
}
