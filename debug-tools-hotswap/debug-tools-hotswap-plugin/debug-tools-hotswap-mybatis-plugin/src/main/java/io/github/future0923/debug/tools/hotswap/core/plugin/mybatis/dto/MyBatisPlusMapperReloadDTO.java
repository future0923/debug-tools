package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto;

/**
 * @author future0923
 */
public class MyBatisPlusMapperReloadDTO {

    private final ClassLoader appClassLoader;

    private final Class<?> clazz;

    private final byte[] bytes;

    private final String path;

    public MyBatisPlusMapperReloadDTO(ClassLoader appClassLoader, Class<?> clazz, byte[] bytes, String path) {
        this.appClassLoader = appClassLoader;
        this.clazz = clazz;
        this.bytes = bytes;
        this.path = path;
    }

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public String getPath() {
        return path;
    }
}
