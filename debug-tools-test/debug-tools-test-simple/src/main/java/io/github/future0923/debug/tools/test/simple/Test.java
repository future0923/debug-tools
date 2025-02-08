package io.github.future0923.debug.tools.test.simple;

import java.util.Map;

/**
 * @author future0923
 */
public class Test {

    public static class TestThread extends Thread {
        @Override
        public void run() {
            while (true) {
                System.out.println("hello world");
                try {
                    Thread.sleep(100000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public void test() {
            System.out.println("123");
        }

        public void exampleMethod(Map<String, Map<String, Map<Integer, String>>> param) {
            // Do something
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new TestThread());
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
