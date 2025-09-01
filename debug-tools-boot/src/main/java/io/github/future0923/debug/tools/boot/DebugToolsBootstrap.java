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
package io.github.future0923.debug.tools.boot;

import io.github.future0923.debug.tools.base.enums.ArgType;
import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.StrUtil;
import io.github.future0923.debug.tools.base.logging.AnsiLog;
import io.github.future0923.debug.tools.base.utils.DebugToolsExecUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsIOUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsJavaVersionUtils;
import io.github.future0923.debug.tools.base.utils.DebugToolsLibUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

/**
 * @author future0923
 */
public class DebugToolsBootstrap {

    public static void main(String[] args) {
        DefaultParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(ArgType.TCP_PORT.getOpt(), ArgType.TCP_PORT.getLongOpt(), ArgType.TCP_PORT.isHasArg(), ArgType.TCP_PORT.getDescription());
        options.addOption(ArgType.HTTP_PORT.getOpt(), ArgType.HTTP_PORT.getLongOpt(), ArgType.HTTP_PORT.isHasArg(), ArgType.HTTP_PORT.getDescription());
        options.addOption(ArgType.PROCESS_ID.getOpt(), ArgType.PROCESS_ID.getLongOpt(), ArgType.PROCESS_ID.isHasArg(), ArgType.PROCESS_ID.getDescription());
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("debug-tools", options);
            return;
        }
        Package bootstrapPackage = DebugToolsBootstrap.class.getPackage();
        if (bootstrapPackage != null) {
            String debugToolsBootVersion = bootstrapPackage.getImplementationVersion();
            if (debugToolsBootVersion != null) {
                AnsiLog.info("debug-tools-boot version: " + debugToolsBootVersion);
            }
        }
        File debugToolsHomeDir = DebugToolsLibUtils.getDebugToolsLibDir();
        File coreJarFile = new File(debugToolsHomeDir, "debug-tools-core.jar");
        FileUtil.del(coreJarFile);
        coreJarFile = DebugToolsFileUtils.getLibResourceJar(DebugToolsBootstrap.class.getClassLoader(), "debug-tools-core");
        File agentJarFile = new File(debugToolsHomeDir, "debug-tools-agent.jar");
        FileUtil.del(agentJarFile);
        agentJarFile = DebugToolsFileUtils.getLibResourceJar(DebugToolsBootstrap.class.getClassLoader(), "debug-tools-agent");
        long pid = -1;
        String pidArg = cmd.getOptionValue(ArgType.PROCESS_ID.getLongOpt());
        if (StrUtil.isNotBlank(pidArg)) {
            pid = Long.parseLong(cmd.getOptionValue(ArgType.PROCESS_ID.getLongOpt()));
        }
        if (pid < 0) {
            try {
                pid = DebugToolsExecUtils.select();
            } catch (InputMismatchException e) {
                AnsiLog.error("Please input an integer to select pid.");
                System.exit(1);
            }
        }
        if (pid < 0) {
            AnsiLog.error("Please select an available pid.");
            System.exit(1);
        }
        AnsiLog.info("Try to attach process " + pid);
        String javaHome = DebugToolsExecUtils.findJavaHome();
        File javaPath = DebugToolsExecUtils.findJava(javaHome);
        if (javaPath == null) {
            throw new IllegalArgumentException(
                    "Can not find java/java.exe executable file under java home: " + javaHome);
        }
        File toolsJar = DebugToolsExecUtils.findToolsJar(javaHome);
        if (DebugToolsJavaVersionUtils.isLessThanJava9()) {
            if (toolsJar == null || !toolsJar.exists()) {
                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
            }
        }
        List<String> command = new ArrayList<>();
        command.add(javaPath.getAbsolutePath());

        if (toolsJar != null && toolsJar.exists()) {
            command.add("-Xbootclasspath/a:" + toolsJar.getAbsolutePath());
        }

        command.add("-jar");
        command.add(coreJarFile.getAbsolutePath());
        command.add("--pid");
        command.add(String.valueOf(pid));
        command.add("--agent");
        command.add(agentJarFile.getAbsolutePath());
        String tcpPort = cmd.getOptionValue(ArgType.TCP_PORT.getLongOpt(), String.valueOf(DebugToolsIOUtils.getAvailablePort(12345)));
        command.add("--" + ArgType.TCP_PORT.getLongOpt());
        command.add(tcpPort);
        String httpPort = cmd.getOptionValue(ArgType.HTTP_PORT.getLongOpt(), String.valueOf(DebugToolsIOUtils.getAvailablePort(22222)));
        command.add("--" + ArgType.HTTP_PORT.getLongOpt());
        command.add(httpPort);
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            final Process proc = pb.start();
            Thread redirectStdout = new Thread(() -> {
                InputStream inputStream = proc.getInputStream();
                try {
                    DebugToolsIOUtils.copy(inputStream, System.out);
                } catch (IOException e) {
                    DebugToolsIOUtils.close(inputStream);
                }
            });

            Thread redirectStderr = new Thread(() -> {
                InputStream inputStream = proc.getErrorStream();
                try {
                    DebugToolsIOUtils.copy(inputStream, System.err);
                } catch (IOException e) {
                    DebugToolsIOUtils.close(inputStream);
                }

            });
            redirectStdout.start();
            redirectStderr.start();
            redirectStdout.join();
            redirectStderr.join();

            int exitValue = proc.exitValue();
            if (exitValue != 0) {
                AnsiLog.error("attach fail, targetPid: " + pid);
                System.exit(1);
            }
        } catch (Throwable e) {
            // ignore
        }
        AnsiLog.info("Attach process {} success. tcp port {}, http port {}.", pid, tcpPort, httpPort);
    }

}
