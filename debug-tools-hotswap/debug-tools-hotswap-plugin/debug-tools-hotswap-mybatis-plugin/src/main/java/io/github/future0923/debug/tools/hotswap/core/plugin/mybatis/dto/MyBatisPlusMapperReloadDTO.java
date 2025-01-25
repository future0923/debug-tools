package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto;

/**
 * @author future0923
 */
public class MyBatisPlusMapperReloadDTO {

    private ClassLoader appClassLoader;

    private Class<?> clazz;

    private byte[] bytes;

    public MyBatisPlusMapperReloadDTO(ClassLoader appClassLoader, Class<?> clazz, byte[] bytes) {
        this.appClassLoader = appClassLoader;
        this.clazz = clazz;
        this.bytes = bytes;
    }

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public void setAppClassLoader(ClassLoader appClassLoader) {
        this.appClassLoader = appClassLoader;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }
}
