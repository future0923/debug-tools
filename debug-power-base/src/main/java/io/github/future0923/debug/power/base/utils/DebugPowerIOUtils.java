package io.github.future0923.debug.power.base.utils;

import io.github.future0923.debug.power.base.logging.Logger;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.util.zip.ZipFile;

/**
 * @author future0923
 */
public class DebugPowerIOUtils {

    private static final Logger logger = Logger.getLogger(DebugPowerIOUtils.class);

    public static int getAvailablePort(int port) {
        while (!isPortAvailable(port)) {
            port++;
        }
        return port;
    }

    public static boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static ServerSocket getServerSocketByDynamicPort(int port) {
        ServerSocket serverSocket = null;
        int i = 0;
        while (i < 5) {
            try {
                serverSocket = new ServerSocket(port + i);
                return serverSocket;
            } catch (Exception e) {
                int currentPort = port + i;
                logger.error("{}端口绑定,失败:{}", currentPort, e);
                ++i;
            }
        }
        return serverSocket;
    }

    public static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int bytesRead;
        byte[] data = new byte[1024];
        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
    }

    public static IOException close(InputStream input) {
        return close((Closeable) input);
    }

    public static IOException close(OutputStream output) {
        return close((Closeable) output);
    }

    public static IOException close(final Reader input) {
        return close((Closeable) input);
    }

    public static IOException close(final Writer output) {
        return close((Closeable) output);
    }

    public static IOException close(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException ioe) {
            return ioe;
        }
        return null;
    }

    // support jdk6
    public static IOException close(final ZipFile zip) {
        try {
            if (zip != null) {
                zip.close();
            }
        } catch (final IOException ioe) {
            return ioe;
        }
        return null;
    }
}
