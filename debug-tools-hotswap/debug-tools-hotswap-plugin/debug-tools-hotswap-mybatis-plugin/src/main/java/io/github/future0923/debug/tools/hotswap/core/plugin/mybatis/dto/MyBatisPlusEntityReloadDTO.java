package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto;

/**
 * @author future0923
 */
public class MyBatisPlusEntityReloadDTO {

    private ClassLoader appClassLoader;

    private Class<?> clazz;

    public MyBatisPlusEntityReloadDTO(ClassLoader appClassLoader, Class<?> clazz) {
        this.appClassLoader = appClassLoader;
        this.clazz = clazz;
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
}
