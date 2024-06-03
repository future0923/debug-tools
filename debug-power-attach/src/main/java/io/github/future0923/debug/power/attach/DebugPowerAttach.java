package io.github.future0923.debug.power.attach;

import io.github.future0923.debug.power.base.utils.DebugPowerFileUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URL;

/**
 * @author future0923
 */
public class DebugPowerAttach {

    private static final String DEBUG_POWER_BOOTSTRAP = "io.github.future0923.debug.power.core.DebugPowerBootstrap";
    private static final String GET_INSTANCE = "getInstance";
    private static final String CALL = "call";

    public static void agentmain(String agentArgs, Instrumentation inst) throws Exception {
        File debugPowerCoreJarFile;
        try {
            URL coreJarUrl = DebugPowerAttach.class.getClassLoader().getResource("lib/debug-power-core.jar");
            if (coreJarUrl == null) {
                throw new IllegalArgumentException("can not getResources debug-power-core.jar from classloader: "
                        + DebugPowerAttach.class.getClassLoader());
            }
            debugPowerCoreJarFile = DebugPowerFileUtils.getTmpLibFile(coreJarUrl.openStream(), "debug-power-core", ".jar");
        } catch (Exception e) {
            throw new IllegalArgumentException("can not getResources debug-power-core.jar from classloader: "
                    + DebugPowerAttach.class.getClassLoader());
        }
        try (DebugPowerClassloader debugPowerClassloader = new DebugPowerClassloader(new URL[]{debugPowerCoreJarFile.toURI().toURL()})) {
            Class<?> bootstrapClass = debugPowerClassloader.loadClass(DEBUG_POWER_BOOTSTRAP);
            Object bootstrap = bootstrapClass.getMethod(GET_INSTANCE).invoke(null);
            bootstrapClass.getMethod(CALL, String.class, Instrumentation.class).invoke(bootstrap, agentArgs, inst);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
