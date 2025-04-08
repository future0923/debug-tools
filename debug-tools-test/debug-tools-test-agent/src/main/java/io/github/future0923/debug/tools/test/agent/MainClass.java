package io.github.future0923.debug.tools.test.agent;

import java.util.Arrays;

/**
 * @author future0923
 */
public class MainClass {

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            System.out.println(Arrays.toString(TargetClass.class.getDeclaredMethods()));
            Thread.sleep(2000);
        }
    }
}
