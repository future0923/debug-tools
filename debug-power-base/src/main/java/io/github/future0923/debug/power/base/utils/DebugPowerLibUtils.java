package io.github.future0923.debug.power.base.utils;

import io.github.future0923.debug.power.base.logging.AnsiLog;

import java.io.File;

/**
 * @author future0923
 */
public class DebugPowerLibUtils {


    private static File DEBUG_POWER_HOME_DIR;
    private static final File DEBUG_POWER_LIB_DIR;

    static {
        String debugPowerLibDirEnv = System.getenv("DEBUG_POWER_HOME_DIR");
        if (debugPowerLibDirEnv != null) {
            DEBUG_POWER_HOME_DIR = new File(debugPowerLibDirEnv);
            AnsiLog.info("DEBUG_POWER_LIB_DIR: " + debugPowerLibDirEnv);
        } else {
            DEBUG_POWER_HOME_DIR = new File(System.getProperty("user.home") + File.separator + ".debugPower");
        }
        try {
            DEBUG_POWER_HOME_DIR.mkdirs();
        } catch (Throwable t) {
            //ignore
        }
        if (!DEBUG_POWER_HOME_DIR.exists()) {
            // try to set a temp directory
            DEBUG_POWER_HOME_DIR = new File(System.getProperty("java.io.tmpdir") + File.separator + ".debugPower");
            try {
                DEBUG_POWER_HOME_DIR.mkdirs();
            } catch (Throwable e) {
                // ignore
            }
        }
        if (!DEBUG_POWER_HOME_DIR.exists()) {
            System.err.println("Can not find directory to save debug power lib. please try to set user home by -Duser.home=");
        }
        DEBUG_POWER_LIB_DIR = new File(DEBUG_POWER_HOME_DIR, "lib");
        if (!DEBUG_POWER_LIB_DIR.exists()) {
            try {
                DEBUG_POWER_LIB_DIR.mkdirs();
            } catch (Throwable e) {
                // ignore
            }
        }

    }

    public static File getDebugPowerHomeDir() {
        return DEBUG_POWER_HOME_DIR;
    }

    public static File getDebugPowerLibDir() {
        return DEBUG_POWER_LIB_DIR;
    }
}
