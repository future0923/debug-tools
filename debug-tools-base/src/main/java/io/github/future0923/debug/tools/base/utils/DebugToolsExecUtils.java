package io.github.future0923.debug.tools.base.utils;

import io.github.future0923.debug.tools.base.logging.AnsiLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * @author future0923
 */
public class DebugToolsExecUtils {

    private static String FOUND_JAVA_HOME = null;

    public static long select() {
        Map<Long, String> processMap = DebugToolsExecUtils.listProcessByJps();
        if (processMap.isEmpty()) {
            AnsiLog.info("Can not find java process. Try to run `jps` command lists the instrumented Java HotSpot VMs on the target system.");
            return -1;
        }
        AnsiLog.info("Found existing java process, please choose one and input the serial number of the process, eg : 1. Then hit ENTER.");
        int count = 1;
        for (String process : processMap.values()) {
            if (count == 1) {
                System.out.println("* [" + count + "]: " + process);
            } else {
                System.out.println("  [" + count + "]: " + process);
            }
            count++;
        }
        String line = new Scanner(System.in).nextLine();
        if (line.trim().isEmpty()) {
            // get the first process id
            return processMap.keySet().iterator().next();
        }

        int choice = new Scanner(line).nextInt();

        if (choice <= 0 || choice > processMap.size()) {
            return -1;
        }

        Iterator<Long> idIter = processMap.keySet().iterator();
        for (int i = 1; i <= choice; ++i) {
            if (i == choice) {
                return idIter.next();
            }
            idIter.next();
        }

        return -1;
    }

    /**
     * <pre>
     * 1. Try to find java home from System Property java.home
     * 2. If jdk > 8, FOUND_JAVA_HOME set to java.home
     * 3. If jdk <= 8, try to find tools.jar under java.home
     * 4. If tools.jar do not exists under java.home, try to find System env JAVA_HOME
     * 5. If jdk <= 8 and tools.jar do not exists under JAVA_HOME, throw IllegalArgumentException
     * </pre>
     *
     * @return JavaHomePath
     */
    public static String findJavaHome() {
        if (FOUND_JAVA_HOME != null) {
            return FOUND_JAVA_HOME;
        }

        String javaHome = System.getProperty("java.home");

        if (DebugToolsJavaVersionUtils.isLessThanJava9()) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                FOUND_JAVA_HOME = javaHome;
                return FOUND_JAVA_HOME;
            }

            if (!toolsJar.exists()) {
                AnsiLog.debug("Can not find tools.jar under java.home: " + javaHome);
                String javaHomeEnv = System.getenv("JAVA_HOME");
                if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
                    AnsiLog.debug("Try to find tools.jar in System Env JAVA_HOME: " + javaHomeEnv);
                    // $JAVA_HOME/lib/tools.jar
                    toolsJar = new File(javaHomeEnv, "lib/tools.jar");
                    if (!toolsJar.exists()) {
                        // maybe jre
                        toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
                    }
                }

                if (toolsJar.exists()) {
                    AnsiLog.info("Found java home from System Env JAVA_HOME: " + javaHomeEnv);
                    FOUND_JAVA_HOME = javaHomeEnv;
                    return FOUND_JAVA_HOME;
                }

                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome
                        + ", please try to start debug-tools-boot with full path java. Such as /opt/jdk/bin/java -jar debug-tools-boot.jar");
            }
        } else {
            FOUND_JAVA_HOME = javaHome;
        }
        return FOUND_JAVA_HOME;
    }

    public static File findJava(String javaHome) {
        String[] paths = { "bin/java", "bin/java.exe", "../bin/java", "../bin/java.exe" };

        List<File> javaList = new ArrayList<File>();
        for (String path : paths) {
            File javaFile = new File(javaHome, path);
            if (javaFile.exists()) {
                AnsiLog.debug("Found java: " + javaFile.getAbsolutePath());
                javaList.add(javaFile);
            }
        }

        if (javaList.isEmpty()) {
            AnsiLog.debug("Can not find java/java.exe under current java home: " + javaHome);
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        if (javaList.size() > 1) {
            javaList.sort((file1, file2) -> {
                try {
                    return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
                } catch (IOException e) {
                    // ignore
                }
                return -1;
            });
        }
        return javaList.get(0);
    }

    public static File findToolsJarNoCheckVersion(String javaHome) {
        File toolsJar = new File(javaHome, "lib/tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            // maybe jre
            toolsJar = new File(javaHome, "../../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
        }
        AnsiLog.debug("Found tools.jar: " + toolsJar.getAbsolutePath());
        return toolsJar;
    }

    public static File findToolsJar(String javaHome) {
        if (DebugToolsJavaVersionUtils.isGreaterThanJava8()) {
            return null;
        }

        File toolsJar = new File(javaHome, "lib/tools.jar");
        if (!toolsJar.exists()) {
            toolsJar = new File(javaHome, "../lib/tools.jar");
        }
        if (!toolsJar.exists()) {
            // maybe jre
            toolsJar = new File(javaHome, "../../lib/tools.jar");
        }

        if (!toolsJar.exists()) {
            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome);
        }

        AnsiLog.debug("Found tools.jar: " + toolsJar.getAbsolutePath());
        return toolsJar;
    }

    public static Map<Long, String> listProcessByJps() {
        Map<Long, String> result = new LinkedHashMap<Long, String>();
        String jps = "jps";
        File jpsFile = findJps();
        if (jpsFile != null) {
            jps = jpsFile.getAbsolutePath();
        }
        AnsiLog.debug("Try use jps to list java process, jps: " + jps);
        String[] command = new String[] { jps, "-l" };
        List<String> lines = runNative(command);
        AnsiLog.debug("jps result: " + lines);
        for (String line : lines) {
            String[] strings = line.trim().split("\\s+");
            if (strings.length < 1) {
                continue;
            }
            try {
                long pid = Long.parseLong(strings[0]);
                if (strings.length >= 2 && isJpsProcess(strings[1])) { // skip jps
                    continue;
                }

                result.put(pid, line);
            } catch (Throwable e) {
                // ignore
            }
        }

        return result;
    }

    private static boolean isJpsProcess(String mainClassName) {
        return "sun.tools.jps.Jps".equals(mainClassName) || "jdk.jcmd/sun.tools.jps.Jps".equals(mainClassName);
    }

    private static File findJps() {
        // Try to find jps under java.home and System env JAVA_HOME
        String javaHome = System.getProperty("java.home");
        String[] paths = { "bin/jps", "bin/jps.exe", "../bin/jps", "../bin/jps.exe" };

        List<File> jpsList = new ArrayList<File>();
        for (String path : paths) {
            File jpsFile = new File(javaHome, path);
            if (jpsFile.exists()) {
                AnsiLog.debug("Found jps: " + jpsFile.getAbsolutePath());
                jpsList.add(jpsFile);
            }
        }

        if (jpsList.isEmpty()) {
            AnsiLog.debug("Can not find jps under :" + javaHome);
            String javaHomeEnv = System.getenv("JAVA_HOME");
            AnsiLog.debug("Try to find jps under env JAVA_HOME :" + javaHomeEnv);
            for (String path : paths) {
                File jpsFile = new File(javaHomeEnv, path);
                if (jpsFile.exists()) {
                    AnsiLog.debug("Found jps: " + jpsFile.getAbsolutePath());
                    jpsList.add(jpsFile);
                }
            }
        }

        if (jpsList.isEmpty()) {
            AnsiLog.debug("Can not find jps under current java home: " + javaHome);
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        if (jpsList.size() > 1) {
            jpsList.sort((file1, file2) -> {
                try {
                    return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
                } catch (IOException e) {
                    // ignore
                }
                return -1;
            });
        }
        return jpsList.get(0);
    }

    public static String exec(String command) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (InputStream inputStream = process.getInputStream()) {
                return new String(DebugToolsIOUtils.readAllBytes(inputStream));
            } catch (Exception ignored) {

            }
        } catch (IOException ignored) {

        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return null;
    }

    public static String execByBash(String command) {
        Process process = null;
        try {
            process = new ProcessBuilder("/bin/bash", "-c", command).redirectErrorStream(true).start();
            try (InputStream inputStream = process.getInputStream()) {
                return new String(DebugToolsIOUtils.readAllBytes(inputStream));
            } catch (Exception ignored) {

            }
        } catch (IOException ignored) {

        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return null;
    }

    public static void exec(String command, Consumer<String> consumer) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (InputStream inputStream = process.getInputStream()) {
                String result = new String(DebugToolsIOUtils.readAllBytes(inputStream));
                consumer.accept(result);
            } catch (Exception ignored) {

            }
        } catch (IOException ignored) {

        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static void execByBash(String command, Consumer<String> consumer) {
        Process process = null;
        try {
            process = new ProcessBuilder("/bin/bash", "-c", command).redirectErrorStream(true).start();
            try (InputStream inputStream = process.getInputStream()) {
                String result = new String(DebugToolsIOUtils.readAllBytes(inputStream));
                consumer.accept(result);
            } catch (Exception ignored) {

            }
        } catch (IOException ignored) {

        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    public static List<String> runNative(String[] cmdToRunWithArgs) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs);
        } catch (SecurityException | IOException e) {
            AnsiLog.trace("Couldn't run command {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(e);
            return new ArrayList<>(0);
        }

        ArrayList<String> sa = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sa.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            AnsiLog.trace("Problem reading output from {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(e);
            return new ArrayList<String>(0);
        } catch (InterruptedException ie) {
            AnsiLog.trace("Problem reading output from {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(ie);
            Thread.currentThread().interrupt();
        } finally {
            DebugToolsIOUtils.close(reader);
        }
        return sa;
    }
}
