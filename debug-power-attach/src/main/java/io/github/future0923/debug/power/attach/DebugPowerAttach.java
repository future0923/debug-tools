package io.github.future0923.debug.power.attach;

import io.github.future0923.debug.power.attach.sqlprint.SqlPrintByteCodeEnhance;
import io.github.future0923.debug.power.base.config.AgentConfig;
import io.github.future0923.debug.power.base.constants.ProjectConstants;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerFileUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author future0923
 */
public class DebugPowerAttach {

    private static final Logger logger = Logger.getLogger(DebugPowerAttach.class);

    private static final AgentConfig agentConfig = AgentConfig.INSTANCE;

    private static final AtomicBoolean isStarted = new AtomicBoolean(false);

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        SqlPrintByteCodeEnhance.enhance(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        if (!isStarted.compareAndSet(false, true)) {
            logger.warning("debug power server already started");
            return;
        }
        try {
            loadCore(inst);
        } catch (Throwable e) {
            logger.error("start debug power server error", e);
            isStarted.set(false);
        }
    }

    private static void loadCore(Instrumentation inst) throws Exception {
        String version = agentConfig.getVersion();
        boolean isUpgrade = !ProjectConstants.VERSION.equals(version);
        if (isUpgrade) {
            agentConfig.setVersion(ProjectConstants.VERSION);
        }
        String corePath = agentConfig.getCorePath();
        File debugPowerCoreJarFile;
        if (ProjectConstants.DEBUG || corePath == null || corePath.isEmpty() || isUpgrade) {
            debugPowerCoreJarFile = createCoreTmpFile();
        } else {
            File file = new File(corePath);
            if (file.exists()) {
                debugPowerCoreJarFile = file;
            } else {
                debugPowerCoreJarFile = createCoreTmpFile();
            }
        }
        agentConfig.store();
        try (DebugPowerClassloader debugPowerClassloader = new DebugPowerClassloader(new URL[]{debugPowerCoreJarFile.toURI().toURL()}, DebugPowerAttach.class.getClassLoader())) {
            debugPowerClassloader.loadAllClasses();
            Class<?> bootstrapClass = debugPowerClassloader.loadClass(ProjectConstants.DEBUG_POWER_BOOTSTRAP);
            Object bootstrap = bootstrapClass.getMethod(ProjectConstants.GET_INSTANCE, Instrumentation.class).invoke(null, inst);
            bootstrapClass.getMethod(ProjectConstants.START).invoke(bootstrap);
        }
    }

    private static File createCoreTmpFile() {
        File debugPowerCoreJarFile;
        try {
            URL coreJarUrl = DebugPowerAttach.class.getClassLoader().getResource(ProjectConstants.SERVER_CORE_JAR_PATH);
            if (coreJarUrl == null) {
                throw new IllegalArgumentException("can not getResources " + ProjectConstants.SERVER_CORE_JAR_PATH + " from classloader: "
                        + DebugPowerAttach.class.getClassLoader());
            }
            debugPowerCoreJarFile = DebugPowerFileUtils.getTmpLibFile(coreJarUrl.openStream(), "debug-power-server", ".jar");
        } catch (Exception e) {
            throw new IllegalArgumentException("can not getResources " + ProjectConstants.SERVER_CORE_JAR_PATH + " from classloader: "
                    + DebugPowerAttach.class.getClassLoader());
        }
        agentConfig.setCorePath(debugPowerCoreJarFile.getAbsolutePath());
        return debugPowerCoreJarFile;
    }

}
