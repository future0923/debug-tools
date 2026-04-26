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

import io.github.future0923.debug.tools.base.constants.ProjectConstants;

import java.io.File;

/**
 * Resolve DebugTools directories with XDG-style layout across platforms.
 *
 * @author future0923
 */
public class DebugToolsPathUtils {

    private static final String ENV_DEBUG_TOOLS_HOME_DIR = "DEBUG_TOOLS_HOME_DIR";
    private static final String ENV_XDG_CONFIG_HOME = "XDG_CONFIG_HOME";
    private static final String ENV_XDG_CACHE_HOME = "XDG_CACHE_HOME";
    private static final String ENV_XDG_DATA_HOME = "XDG_DATA_HOME";
    private static final String ENV_XDG_STATE_HOME = "XDG_STATE_HOME";

    private DebugToolsPathUtils() {
    }

    public static File getConfigDir() {
        if (!isXdgEnabled()) {
            return getLegacyConfigDir();
        }
        return resolveXdgDir(ENV_XDG_CONFIG_HOME, ".config");
    }

    public static File getCacheDir() {
        if (!isXdgEnabled()) {
            return getLegacyConfigDir();
        }
        return resolveXdgDir(ENV_XDG_CACHE_HOME, ".cache");
    }

    public static File getDataDir() {
        if (!isXdgEnabled()) {
            return getLegacyConfigDir();
        }
        return resolveXdgDir(ENV_XDG_DATA_HOME, ".local" + File.separator + "share");
    }

    public static File getStateDir() {
        if (!isXdgEnabled()) {
            return getLegacyConfigDir();
        }
        return resolveXdgDir(ENV_XDG_STATE_HOME, ".local" + File.separator + "state");
    }

    public static File getLibHomeDir() {
        String debugToolsHome = System.getenv(ENV_DEBUG_TOOLS_HOME_DIR);
        if (isNotBlank(debugToolsHome)) {
            return new File(debugToolsHome);
        }
        if (isXdgEnabled()) {
            return getCacheDir();
        }
        return getLegacyLibDir();
    }

    public static File getLegacyConfigDir() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            return new File(ProjectConstants.NAME);
        }
        return new File(userHome, ProjectConstants.NAME);
    }

    public static File getLegacyLibDir() {
        String userHome = System.getProperty("user.home");
        if (userHome == null) {
            return new File(".debugTools");
        }
        return new File(userHome, ".debugTools");
    }

    private static boolean isXdgEnabled() {
        return true;
    }

    private static File resolveXdgDir(String envVar, String fallbackRelative) {
        String basePath = System.getenv(envVar);
        File baseDir;
        if (isNotBlank(basePath)) {
            baseDir = new File(basePath);
        } else {
            String userHome = System.getProperty("user.home");
            if (userHome == null) {
                baseDir = new File(".");
            } else {
                baseDir = new File(userHome, fallbackRelative);
            }
        }
        return new File(baseDir, ProjectConstants.NAME);
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}





