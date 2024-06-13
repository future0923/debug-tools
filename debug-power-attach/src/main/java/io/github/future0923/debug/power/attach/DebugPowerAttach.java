package io.github.future0923.debug.power.attach;

import io.github.future0923.debug.power.attach.sqlprint.SqlPrintByteCodeEnhance;
import io.github.future0923.debug.power.base.DebugPower;
import io.github.future0923.debug.power.base.config.AgentConfig;
import io.github.future0923.debug.power.base.constants.ProjectConstants;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerFileUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author future0923
 */
public class DebugPowerAttach {

    private static final Logger logger = Logger.getLogger(DebugPowerAttach.class);

    private static final Map<String, Class<?>> loadClassMap = new ConcurrentHashMap<>();

    private static final AgentConfig agentConfig = AgentConfig.INSTANCE;

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        SqlPrintByteCodeEnhance.enhance(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        init(ProjectConstants.DEBUG_POWER_BOOTSTRAP, bootstrapClass -> {
            try {
                Object bootstrap = bootstrapClass.getMethod(ProjectConstants.GET_INSTANCE, Instrumentation.class).invoke(null, inst);
                bootstrapClass.getMethod(ProjectConstants.START).invoke(bootstrap);
            } catch (Exception e) {
                logger.error("call target method error", e);
            }
        });
    }

    private static void init(String loaderClassName, Consumer<Class<?>> consumer) {
        try {
            Class<?> loadClass = loadClassMap.get(loaderClassName);
            if (loadClass != null) {
                consumer.accept(loadClass);
                return;
            }
        } catch (Throwable e) {
            // ignore
        }
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
        try (DebugPowerClassloader debugPowerClassloader = new DebugPowerClassloader(new URL[]{debugPowerCoreJarFile.toURI().toURL()}, DebugPower.class.getClassLoader())) {
            Class<?> loadClass = debugPowerClassloader.loadClass(loaderClassName);
            debugPowerClassloader.loadAllClasses();
            loadClassMap.put(loaderClassName, loadClass);
            consumer.accept(loadClass);
        } catch (Exception e) {
            logger.error("load error", e);
        }
    }

    private static File createCoreTmpFile() {
        File debugPowerCoreJarFile;
        try {
            URL coreJarUrl = DebugPowerAttach.class.getClassLoader().getResource(ProjectConstants.RESOURCE_CORE_PATH);
            if (coreJarUrl == null) {
                throw new IllegalArgumentException("can not getResources debug-power-core.jar from classloader: "
                        + DebugPowerAttach.class.getClassLoader());
            }
            debugPowerCoreJarFile = DebugPowerFileUtils.getTmpLibFile(coreJarUrl.openStream(), "debug-power-core", ".jar");
        } catch (Exception e) {
            throw new IllegalArgumentException("can not getResources debug-power-core.jar from classloader: "
                    + DebugPowerAttach.class.getClassLoader());
        }
        agentConfig.setCorePath(debugPowerCoreJarFile.getAbsolutePath());
        return debugPowerCoreJarFile;
    }

}
