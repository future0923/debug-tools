/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
