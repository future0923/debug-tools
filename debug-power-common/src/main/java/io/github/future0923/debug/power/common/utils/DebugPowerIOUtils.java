package io.github.future0923.debug.power.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author future0923
 */
public class DebugPowerIOUtils {

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
}
