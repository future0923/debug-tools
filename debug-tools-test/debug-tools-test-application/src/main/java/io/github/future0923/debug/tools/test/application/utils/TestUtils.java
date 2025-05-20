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
