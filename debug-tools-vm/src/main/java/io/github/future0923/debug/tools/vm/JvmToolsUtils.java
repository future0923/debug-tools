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
package io.github.future0923.debug.tools.vm;

import io.github.future0923.debug.tools.base.config.AgentConfig;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsOSUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author future0923
 */
public class JvmToolsUtils {

    private static VmTool instance;

    private static boolean init = false;

    private static boolean load = false;

    public static synchronized void init() {
        if (init) {
            return;
        }
        String jniPath = AgentConfig.INSTANCE.getJniLibraryPath();
        // 不是调试模式 && 没有升级版本 && jniPath不为空 && 文件存在 && 未加载
        if (!ProjectConstants.DEBUG && !AgentConfig.INSTANCE.isUpgrade() && DebugToolsStringUtils.isNotBlank(jniPath) && DebugToolsFileUtils.exist(jniPath) && !load) {
            initVmTool(jniPath);
            return;
        }
        String libName;
        String arch = System.getProperty("os.arch").toLowerCase();
        boolean isArm = arch.contains("aarch64") || arch.contains("arm64"); // 兼容不同JVM下的arm64标识
        if (DebugToolsOSUtils.isMac()) {
            libName = isArm ? "libJniLibrary-arm64.dylib" : "libJniLibrary.dylib";
        } else if (DebugToolsOSUtils.isLinux()) {
            libName = "libJniLibrary-x64.dll";
        } else if (DebugToolsOSUtils.isWindows()) {
            libName = "libJniLibrary-x64.dll";
        } else {
            throw new IllegalStateException("unsupported os");
        }

        String libPath = "lib/" + libName;
        URL jniLibraryUrl = JvmToolsUtils.class.getClassLoader().getResource(libPath);
        if (jniLibraryUrl == null) {
            throw new IllegalArgumentException("can not getResources " + libName + " from classloader: "
                    + JvmToolsUtils.class.getClassLoader());
        }
        File jniLibraryFile;
        try {
            jniLibraryFile = DebugToolsFileUtils.getTmpLibFile(jniLibraryUrl.openStream(), "DebugToolsJniLibrary", DebugToolsFileUtils.extName(libName, true));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initVmTool(jniLibraryFile.getAbsolutePath());
        AgentConfig.INSTANCE.setJniLibraryPathAndStore(jniLibraryFile.getAbsolutePath());
    }

    public static <T> T[] getInstances(Class<T> targetClass) {
        return instance.getInstances(targetClass);
    }

    private static void initVmTool(String libPath) {
        instance = VmTool.getInstance(libPath);
        if (instance == null) {
            throw new IllegalStateException("VmToolUtils init fail. libPath: " + libPath);
        }
        init = true;
        load = true;
    }
}
