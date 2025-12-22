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
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
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

    private static final Logger logger = Logger.getLogger(JvmToolsUtils.class);

    public static synchronized void init() {
        if (init) {
            return;
        }

        // 目前只针对mac特殊处理
        if (DebugToolsOSUtils.isMac() && changeJdk()) {
            AgentConfig.INSTANCE.setCurrentOsArch(DebugToolsOSUtils.arch());
            storeLib(getLibName());
        }

        String jniPath = AgentConfig.INSTANCE.getJniLibraryPath();
        // 不是调试模式 && 没有升级版本 && jniPath不为空 && 文件存在 && 未加载
        if (!ProjectConstants.DEBUG && !AgentConfig.INSTANCE.isUpgrade() && DebugToolsStringUtils.isNotBlank(jniPath) && DebugToolsFileUtils.exist(jniPath) && !load) {
            initVmTool(jniPath);
            return;
        }

        storeLib(getLibName());
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

    private static String getLibName() {
        if (DebugToolsOSUtils.isMac()) {
            return DebugToolsOSUtils.isArm64() ? "libJniLibrary-arm64.dylib" : "libJniLibrary.dylib";
        } else if (DebugToolsOSUtils.isLinux()) {
            return "libJniLibrary-x64.so";
        } else if (DebugToolsOSUtils.isWindows()) {
            return "libJniLibrary-x64.dll";
        } else {
            throw new IllegalStateException("unsupported os");
        }
    }

    private static void storeLib(String libName) {
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

    private static boolean changeJdk() {
        String storedArch = AgentConfig.INSTANCE.getCurrentOsArch();
        // 第一次运行
        if (StrUtil.isBlank(storedArch)) {
            logger.info("DebugTools first use, current os arch:", DebugToolsOSUtils.arch());
            return true;
        }

        boolean changed = !StrUtil.equals(DebugToolsOSUtils.arch(), storedArch);

        if (changed) {
            logger.info("Jvm os arch has changed,current os arch: {},stored os arch: {}", DebugToolsOSUtils.arch(), storedArch);
        }
        return changed;
    }
}
