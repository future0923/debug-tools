package io.github.future0923.debug.power.test.application.utils;

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
}
