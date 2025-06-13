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
        if (DebugToolsOSUtils.isMac()) {
            libName = "libJniLibrary.dylib";
        } else if (DebugToolsOSUtils.isLinux()) {
            libName = "libJniLibrary-x64.so";
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
