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

import java.io.File;
import java.util.Locale;

/**
 * @author future0923
 */
public class DebugToolsOSUtils {

    private static final String OPERATING_SYSTEM_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    private static final String OPERATING_SYSTEM_ARCH = System.getProperty("os.arch").toLowerCase(Locale.ENGLISH);
    private static final String UNKNOWN = "unknown";

    static PlatformEnum platform;

    static String arch;

    static {
        if (OPERATING_SYSTEM_NAME.startsWith("linux")) {
            platform = PlatformEnum.LINUX;
        } else if (OPERATING_SYSTEM_NAME.startsWith("mac") || OPERATING_SYSTEM_NAME.startsWith("darwin")) {
            platform = PlatformEnum.MACOS;
        } else if (OPERATING_SYSTEM_NAME.startsWith("windows")) {
            platform = PlatformEnum.WINDOWS;
        } else {
            platform = PlatformEnum.UNKNOWN;
        }

        arch = normalizeArch();
    }

    private DebugToolsOSUtils() {
    }

    public static boolean isWindows() {
        return platform == PlatformEnum.WINDOWS;
    }

    public static boolean isLinux() {
        return platform == PlatformEnum.LINUX;
    }

    public static boolean isMac() {
        return platform == PlatformEnum.MACOS;
    }

    public static boolean isCygwinOrMinGW() {
        if (isWindows()) {
            if ((System.getenv("MSYSTEM") != null && System.getenv("MSYSTEM").startsWith("MINGW"))
                    || "/bin/bash".equals(System.getenv("SHELL"))) {
                return true;
            }
        }
        return false;
    }

    public static String arch() {
        return arch;
    }

    public static boolean isArm32() {
        return "arm_32".equals(arch);
    }

    public static boolean isArm64() {
        return "aarch_64".equals(arch);
    }

    public static boolean isX86() {
        return "x86_32".equals(arch);
    }

    public static boolean isX86_64() {
        return "x86_64".equals(arch);
    }

    private static String normalizeArch() {
        String value = normalize(OPERATING_SYSTEM_ARCH);
        if (value.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
            return "x86_64";
        }
        if (value.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
            return "x86_32";
        }
        if (value.matches("^(ia64w?|itanium64)$")) {
            return "itanium_64";
        }
        if ("ia64n".equals(value)) {
            return "itanium_32";
        }
        if (value.matches("^(sparc|sparc32)$")) {
            return "sparc_32";
        }
        if (value.matches("^(sparcv9|sparc64)$")) {
            return "sparc_64";
        }
        if (value.matches("^(arm|arm32)$")) {
            return "arm_32";
        }
        if ("aarch64".equals(value)) {
            return "aarch_64";
        }
        if (value.matches("^(mips|mips32)$")) {
            return "mips_32";
        }
        if (value.matches("^(mipsel|mips32el)$")) {
            return "mipsel_32";
        }
        if ("mips64".equals(value)) {
            return "mips_64";
        }
        if ("mips64el".equals(value)) {
            return "mipsel_64";
        }
        if (value.matches("^(ppc|ppc32)$")) {
            return "ppc_32";
        }
        if (value.matches("^(ppcle|ppc32le)$")) {
            return "ppcle_32";
        }
        if ("ppc64".equals(value)) {
            return "ppc_64";
        }
        if ("ppc64le".equals(value)) {
            return "ppcle_64";
        }
        if ("s390".equals(value)) {
            return "s390_32";
        }
        if ("s390x".equals(value)) {
            return "s390_64";
        }
        return value;
    }

    public static boolean isMuslLibc() {
        File ld_musl_x86_64_file = new File("/lib/ld-musl-x86_64.so.1");
        File ld_musl_aarch64_file = new File("/lib/ld-musl-aarch64.so.1");

        if(ld_musl_x86_64_file.exists() || ld_musl_aarch64_file.exists()){
            return true;
        }

        return false;
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
    }

    public enum PlatformEnum {
        WINDOWS,
        LINUX,
        MACOS,
        UNKNOWN;
    }
}
