package io.github.future0923.debug.power.common.utils;

import io.github.future0923.debug.power.base.utils.DebugPowerStringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

/**
 * @author future0923
 */
public class DebugPowerJvmUtils {

    public static String getMainClass() {
        String javaClassPath = getJavaClassPath();
        try (JarFile jarFile = new JarFile(new File(javaClassPath))) {
            Attributes attributes = jarFile.getManifest().getMainAttributes();
            String startClass = attributes.getValue("Start-Class");
            if (DebugPowerStringUtils.isNotBlank(startClass)) {
                return startClass;
            }
            String mainClass = attributes.getValue("Main-Class");
            if (DebugPowerStringUtils.isNotBlank(mainClass)) {
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
                return sunJavaCommand.substring(0, index);
            }
            return sunJavaCommand;
        }
        return "DebugPowerAgent";
    }

    public static String getSunJavaCommand() {
        return ManagementFactory.getRuntimeMXBean().getSystemProperties().get("sun.java.command");
    }
}
