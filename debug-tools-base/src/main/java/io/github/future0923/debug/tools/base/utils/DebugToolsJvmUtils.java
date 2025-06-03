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

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author future0923
 */
public class DebugToolsJvmUtils {

    public static String getMainClass() {
        String javaClassPath = getJavaClassPath();
        try (JarFile jarFile = new JarFile(new File(javaClassPath))) {
            Attributes attributes = jarFile.getManifest().getMainAttributes();
            String startClass = attributes.getValue("Start-Class");
            if (DebugToolsStringUtils.isNotBlank(startClass)) {
                return startClass;
            }
            String mainClass = attributes.getValue("Main-Class");
            if (DebugToolsStringUtils.isNotBlank(mainClass)) {
                return mainClass;
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public static String getJavaClassPath() {
        return ManagementFactory.getRuntimeMXBean().getSystemProperties().get("java.class.path");
    }

    public static String getApplicationName() {
        String mainClass = getMainClass();
        if (mainClass != null) {
            return mainClass;
        }
        String sunJavaCommand = getSunJavaCommand();
        if (sunJavaCommand != null) {
            int index = sunJavaCommand.indexOf(".jar");
            if (index != -1) {
                return FileUtil.getName(sunJavaCommand);
            }
            return sunJavaCommand;
        }
        return "DebugToolsAgent";
    }

    public static String getSunJavaCommand() {
        return ManagementFactory.getRuntimeMXBean().getSystemProperties().get("sun.java.command");
    }
}
