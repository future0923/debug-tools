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
package io.github.future0923.debug.tools.attach;

import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.config.AgentConfig;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.RuntimeUtil;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsExecUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.base.utils.HotswapIgnoreStaticFieldUtils;
import io.github.future0923.debug.tools.hotswap.core.HotswapAgent;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.sql.SqlPrintByteCodeEnhance;
import io.github.future0923.debug.tools.vm.JvmToolsUtils;
import javassist.CtClass;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

/**
 * DebugTools Agent
 *
 * @author future0923
 */
public class DebugToolsAttach {

    private static final Logger logger = Logger.getLogger(DebugToolsAttach.class);

    /** tools.jar 成功加载后由两个 agent 入口共享，失败时仍允许重试。 */
    private static boolean toolsJarLoaded;

    /**
     * 启动入口
     *
     * @param agentArgs 参数
     * @param inst      instrumentation
     * @throws Exception 启动失败
     */
    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        DebugToolsStartupHandshake.starting();
        AgentArgs parse = AgentArgs.parse(agentArgs);
        applyRuntimeConfig(parse);
        if (ProjectConstants.DEBUG) {
            // 开启javassist debug
            CtClass.debugDump = "debug/javassist";
            // 开启cglib debug
            System.setProperty("cglib.debugLocation", "debug/cglib");
        }
        JvmToolsUtils.init();
        AgentConfig.INSTANCE.createExtensionJar();
        SqlPrintByteCodeEnhance.enhance(inst, parse);
        if (Boolean.parseBoolean(parse.getHotswap()) || Boolean.parseBoolean(parse.getAutoHotswap())) {
            HotswapIgnoreStaticFieldUtils.create(parse.getIgnoreStaticFieldPath());
            HotswapAgent.init(parse, inst);
        }
        if (Objects.equals(parse.getServer(), "true")) {
            startServer(parse, inst);
            DebugToolsStartupHandshake.ready();
        }
        if (Objects.equals(parse.getAutoAttach(), "true")) {
            autoAttach();
        }
    }

    /**
     * Attach agent 入口
     *
     * @param agentArgs 参数
     * @param inst      instrumentation
     * @throws Exception 启动失败
     */
    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        AgentArgs parse = AgentArgs.parse(agentArgs);
        applyRuntimeConfig(parse);
        AgentConfig.INSTANCE.createExtensionJar();
        startServer(parse, inst);
    }

    /** 两种加载方式都先应用日志级别，再准备动态编译环境。 */
    private static void applyRuntimeConfig(AgentArgs agentArgs) {
        if (agentArgs.getLogLevel() != null) {
            Logger.setLevel(agentArgs.getLogLevel());
        }
        String javaHome = System.getProperty("java.home");
        logger.info("JAVA_HOME:{}", javaHome);
        loadToolsJar(javaHome);
    }

    /**
     * 如果能找到 tools.jar 则载入
     *
     * @param javaHome java home
     */
    private static synchronized void loadToolsJar(String javaHome) {
        if (toolsJarLoaded) {
            return;
        }
        try {
            Class.forName("com.sun.tools.javac.processing.JavacProcessingEnvironment");
            toolsJarLoaded = true;
        } catch (ClassNotFoundException e) {
            File toolsJar;
            try {
                toolsJar = DebugToolsExecUtils.findToolsJar(javaHome);
            } catch (Exception ee) {
                // 小于等于8找不到时给提示
                logger.warning("The tools.jar file was not found, so remote dynamic compilation cannot be used. If you want to use remote dynamic compilation, please only use the jdk environment, not the jre. {}", ee.getMessage());
                return;
            }
            if (toolsJar != null) {
                try {
                    URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                    Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                    addURL.setAccessible(true);
                    addURL.invoke(sysLoader, toolsJar.toURI().toURL());
                    // 确认编译器可用后才标记成功，避免失败后永久跳过加载。
                    Class.forName("com.sun.tools.javac.processing.JavacProcessingEnvironment");
                    toolsJarLoaded = true;
                    logger.info("Loaded tools.jar file in {}", sysLoader.getClass().getName());
                } catch (Exception ex) {
                    logger.warning("Failed to load the tools.jar file, so remote dynamic compilation cannot be used. If you want to use remote dynamic compilation, please only use the jdk environment, not the jre. {}", ex.getMessage());
                }
            }
        }
    }

    /**
     * 启动server服务
     *
     * @param agentArgs 参数
     * @param inst      instrumentation
     */
    private static void startServer(AgentArgs agentArgs, Instrumentation inst) {
        DebugToolsBootstrap.getInstance(inst).start(agentArgs);
    }

    private static void autoAttach() throws IOException {
        String pid = String.valueOf(RuntimeUtil.getPid());
        FileUtil.writeUtf8String("1", DebugToolsFileUtils.getAutoAttachFile(pid));
        // 保留全局标记，兼容尚未支持 PID 就绪文件的旧版 IDEA 插件。
        FileUtil.writeUtf8String("1", DebugToolsFileUtils.getAutoAttachFile());
    }
}
