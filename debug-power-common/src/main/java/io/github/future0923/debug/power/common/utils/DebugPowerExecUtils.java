package io.github.future0923.debug.power.common.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * @author future0923
 */
public class DebugPowerExecUtils {

    public static String exec(String command) {
        Process process = null;
        try {
            process = new ProcessBuilder(command).redirectErrorStream(true).start();
            try (InputStream inputStream = process.getInputStream()) {
                return new String(DebugPowerIOUtils.readAllBytes(inputStream));
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
                return new String(DebugPowerIOUtils.readAllBytes(inputStream));
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
                String result = new String(DebugPowerIOUtils.readAllBytes(inputStream));
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
                String result = new String(DebugPowerIOUtils.readAllBytes(inputStream));
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
}
