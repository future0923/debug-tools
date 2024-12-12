package io.github.future0923.debug.tools.attach;

import io.github.future0923.debug.tools.attach.sqlprint.SqlPrintByteCodeEnhance;
import io.github.future0923.debug.tools.base.classloader.DebugToolsClassLoader;
import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.base.config.AgentConfig;
import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsFileUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author future0923
 */
public class DebugToolsAttach {

    private static final Logger logger = Logger.getLogger(DebugToolsAttach.class);

    private static final AgentConfig agentConfig = AgentConfig.INSTANCE;

    private static final AtomicBoolean isStarted = new AtomicBoolean(false);

    private static Class<?> bootstrapClass;

    private static Object bootstrap;

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        SqlPrintByteCodeEnhance.enhance(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        try {
            loadCore(inst);
        } catch (Throwable e) {
            logger.error("start debug tools server error", e);
            isStarted.set(false);
            return;
        }
        bootstrapClass.getMethod(ProjectConstants.START, String.class).invoke(bootstrap, agentArgs);
    }

    private static void loadCore(Instrumentation inst) throws Exception {
        if (!isStarted.compareAndSet(false, true)) {
            return;
        }
        String version = agentConfig.getVersion();
        boolean isUpgrade = !ProjectConstants.VERSION.equals(version);
        if (isUpgrade) {
            agentConfig.setVersion(ProjectConstants.VERSION);
        }
        String corePath = agentConfig.getCorePath();
        File debugToolsCoreJarFile;
        if (ProjectConstants.DEBUG || corePath == null || corePath.isEmpty() || isUpgrade) {
            debugToolsCoreJarFile = createCoreTmpFile();
        } else {
            File file = new File(corePath);
            if (file.exists()) {
                debugToolsCoreJarFile = file;
            } else {
                debugToolsCoreJarFile = createCoreTmpFile();
            }
        }
        agentConfig.store();
        try {
            DebugToolsClassLoader debugToolsClassloader = new DebugToolsClassLoader(new URL[]{debugToolsCoreJarFile.toURI().toURL()}, DefaultClassLoader.getDefaultClassLoader(inst));
            debugToolsClassloader.loadAllClasses();
            bootstrapClass = debugToolsClassloader.loadClass(ProjectConstants.DEBUG_TOOLS_BOOTSTRAP);
            bootstrap = bootstrapClass.getMethod(ProjectConstants.GET_INSTANCE, Instrumentation.class, ClassLoader.class).invoke(null, inst, debugToolsClassloader);
        } catch (ClassNotFoundException ignored) {

        }
    }

    private static File createCoreTmpFile() {
        File debugToolsCoreJarFile;
        try {
            URL coreJarUrl = DebugToolsAttach.class.getClassLoader().getResource(ProjectConstants.SERVER_CORE_JAR_PATH);
            if (coreJarUrl == null) {
                throw new IllegalArgumentException("can not getResources " + ProjectConstants.SERVER_CORE_JAR_PATH + " from classloader: "
                        + DebugToolsAttach.class.getClassLoader());
            }
            debugToolsCoreJarFile = DebugToolsFileUtils.getTmpLibFile(coreJarUrl.openStream(), "debug-tools-server", ".jar");
        } catch (Exception e) {
            throw new IllegalArgumentException("can not getResources " + ProjectConstants.SERVER_CORE_JAR_PATH + " from classloader: "
                    + DebugToolsAttach.class.getClassLoader());
        }
        agentConfig.setCorePath(debugToolsCoreJarFile.getAbsolutePath());
        return debugToolsCoreJarFile;
    }
}