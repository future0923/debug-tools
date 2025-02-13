package io.github.future0923.debug.tools.test.simple;

import java.util.Map;

/**
 * @author future0923
 */
public class TestThread extends Thread {

    private static final String aa = "a";
    private static final int bb = 2;
    private static int cc;

    static {
        cc = 3;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Object test() {
        System.out.println(aa);
        System.out.println(bb);
        System.out.println(cc);
        return aa + " " + bb + " " + cc;
    }

    public void exampleMethod(Map<String, Map<String, Map<Integer, String>>> param) {
        // Do something
    }
}
