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
