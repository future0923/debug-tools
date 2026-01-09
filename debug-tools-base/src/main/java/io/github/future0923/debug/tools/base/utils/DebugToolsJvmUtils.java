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

import io.github.future0923.debug.tools.base.config.AgentConfig;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author future0923
 */
public class DebugToolsJvmUtils {

    private static final Logger logger = Logger.getLogger(DebugToolsJvmUtils.class);


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

    public static boolean changeJdk(Boolean ignoreChangeLog) {
        String storedArch = AgentConfig.INSTANCE.getCurrentOsArch();
        // 第一次运行
        if (StrUtil.isBlank(storedArch)) {
            logger.info("DebugTools first use, current os arch:", DebugToolsOSUtils.arch());
            return true;
        }

        boolean changed = !StrUtil.equals(DebugToolsOSUtils.arch(), storedArch);

        if (changed && !ignoreChangeLog) {
            logger.info("Jvm os arch has changed,current os arch: {},stored os arch: {}", DebugToolsOSUtils.arch(), storedArch);
        }
        return changed;
    }
}
