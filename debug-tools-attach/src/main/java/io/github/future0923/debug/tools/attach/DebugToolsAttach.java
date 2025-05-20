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
package io.github.future0923.debug.tools.attach;

import io.github.future0923.debug.tools.base.config.AgentArgs;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsExecUtils;
import io.github.future0923.debug.tools.hotswap.core.HotswapAgent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import io.github.future0923.debug.tools.sql.SqlPrintByteCodeEnhance;

import java.io.File;
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

    /**
     * 启动入口
     *
     * @param agentArgs 参数
     * @param inst      instrumentation
     * @throws Exception 启动失败
     */
    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        String javaHome = System.getProperty("java.home");
        logger.info("JAVA_HOME:{}", javaHome);
        loadToolsJar(javaHome);
        if (ProjectConstants.DEBUG) {
            // 开启javassist debug
            CtClass.debugDump = "debug/javassist";
            // 开启cglib debug
            System.setProperty("cglib.debugLocation", "debug/cglib");
        }
        AgentArgs parse = AgentArgs.parse(agentArgs);
        if (parse.getLogLevel() != null) {
            Logger.setLevel(parse.getLogLevel());
        }
        if (Objects.equals(parse.getPrintSql(), "true")) {
            SqlPrintByteCodeEnhance.enhance(inst);
        }
        if (Objects.equals(parse.getHotswap(), "true")) {
            HotswapAgent.init(parse, inst);
        }
        if (Objects.equals(parse.getServer(), "true")) {
            startServer(parse, inst);
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
        startServer(AgentArgs.parse(agentArgs), inst);
    }

    /**
     * 如果能找到 tools.jar 则载入
     *
     * @param javaHome java home
     */
    private static void loadToolsJar(String javaHome) {
        try {
            Class.forName("com.sun.tools.javac.processing.JavacProcessingEnvironment");
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
}