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
package io.github.future0923.debug.tools.base.utils;

import java.util.Properties;

/**
 *
 * @author hengyunabc 2018-11-21
 *
 */
public class DebugToolsJavaVersionUtils {
    private static final String VERSION_PROP_NAME = "java.specification.version";
    private static final String JAVA_VERSION_STR = System.getProperty(VERSION_PROP_NAME);
    private static final float JAVA_VERSION = Float.parseFloat(JAVA_VERSION_STR);

    private DebugToolsJavaVersionUtils() {
    }

    public static String javaVersionStr() {
        return JAVA_VERSION_STR;
    }

    public static String javaVersionStr(Properties props) {
        return (null != props) ? props.getProperty(VERSION_PROP_NAME): null;
    }

    public static float javaVersion() {
        return JAVA_VERSION;
    }

    public static boolean isJava6() {
        return "1.6".equals(JAVA_VERSION_STR);
    }

    public static boolean isJava7() {
        return "1.7".equals(JAVA_VERSION_STR);
    }

    public static boolean isJava8() {
        return "1.8".equals(JAVA_VERSION_STR);
    }

    public static boolean isJava9() {
        return "9".equals(JAVA_VERSION_STR);
    }

    public static boolean isLessThanJava9() {
        return JAVA_VERSION < 9.0f;
    }

    public static boolean isGreaterThanJava7() {
        return JAVA_VERSION > 1.7f;
    }

    public static boolean isGreaterThanJava8() {
        return JAVA_VERSION > 1.8f;
    }

    public static boolean isGreaterThanJava11() {
        return JAVA_VERSION > 11.0f;
    }
}
