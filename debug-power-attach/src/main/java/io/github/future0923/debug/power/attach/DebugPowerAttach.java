package io.github.future0923.debug.power.attach;

import io.github.future0923.debug.power.attach.sqlprint.SqlPrintByteCodeEnhance;
import io.github.future0923.debug.power.base.DebugPower;
import io.github.future0923.debug.power.base.constants.ProjectConstants;
import io.github.future0923.debug.power.base.constants.PropertiesConstants;
import io.github.future0923.debug.power.base.logging.Logger;
import io.github.future0923.debug.power.base.utils.DebugPowerFileUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author future0923
 */
public class DebugPowerAttach {

    private static final Logger logger = Logger.getLogger(DebugPowerAttach.class);

    private static final Map<String, Class<?>> loadClassMap = new ConcurrentHashMap<>();

    private static final Properties properties = new Properties();

    public static void premain(String agentArgs, Instrumentation inst) throws Exception {
        SqlPrintByteCodeEnhance.enhance(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        init(ProjectConstants.DEBUG_POWER_BOOTSTRAP, bootstrapClass -> {
            try {
                Object bootstrap = bootstrapClass.getMethod(ProjectConstants.GET_INSTANCE, Properties.class).invoke(null, properties);
                bootstrapClass.getMethod(ProjectConstants.CALL, String.class, Instrumentation.class).invoke(bootstrap, agentArgs, inst);
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
        String homeDir = System.getProperty("user.home");
        File cacheProperties = new File(homeDir + "/" + ProjectConstants.NAME + "/debug-power-cache.properties");
        if (!cacheProperties.exists()) {
            DebugPowerFileUtils.touch(cacheProperties);
        }
        try {
            properties.load(cacheProperties.toURI().toURL().openStream());
        } catch (IOException e) {
            logger.error("load properties error", e);
            return;
        }
        String version = properties.getProperty(PropertiesConstants.VERSION);
        boolean isUpgrade = !ProjectConstants.VERSION.equals(version);
        if (isUpgrade) {
            properties.setProperty(PropertiesConstants.VERSION, ProjectConstants.VERSION);
        }
        String corePath = properties.getProperty(PropertiesConstants.CORE_PATH);
        File debugPowerCoreJarFile;
        if (ProjectConstants.DEBUG || corePath == null || corePath.isEmpty() || isUpgrade) {
            debugPowerCoreJarFile = createCoreTmpFile(properties, cacheProperties);
        } else {
            File file = new File(corePath);
            if (file.exists()) {
                debugPowerCoreJarFile = file;
            } else {
                debugPowerCoreJarFile = createCoreTmpFile(properties, cacheProperties);
            }
        }
        try (DebugPowerClassloader debugPowerClassloader = new DebugPowerClassloader(new URL[]{debugPowerCoreJarFile.toURI().toURL()}, DebugPower.class.getClassLoader())) {
            Class<?> loadClass = debugPowerClassloader.loadClass(loaderClassName);
            loadClassMap.put(loaderClassName, loadClass);
            consumer.accept(loadClass);
        } catch (Exception e) {
            logger.error("load error", e);
        }
    }

    private static File createCoreTmpFile(Properties properties, File cacheProperties) {
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
        properties.setProperty(PropertiesConstants.CORE_PATH, debugPowerCoreJarFile.getAbsolutePath());
        try {
            OutputStream outputStream = Files.newOutputStream(cacheProperties.toPath());
            properties.store(outputStream, "config debug power core path");
            outputStream.close();
        } catch (IOException e) {
            logger.error("store properties error", e);
        }
        return debugPowerCoreJarFile;
    }

}
