package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto;

/**
 * @author future0923
 */
public class MyBatisSpringMapperReloadDTO {

    private final String className;

    private final byte[] bytes;

    private final String path;

    public MyBatisSpringMapperReloadDTO(String className, byte[] bytes, String path) {
        this.className = className;
        this.bytes = bytes;
        this.path = path;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getClassName() {
        return className;
    }

    public String getPath() {
        return path;
    }
}
