package io.github.future0923.debug.tools.common.classloader;

/**
 * 默认的类加载器，主要是loadClass方法变成public
 *
 * @author future0923
 */
public class DefaultClassLoader extends ClassLoader {

    public DefaultClassLoader(ClassLoader defaultClassLoader) {
        super(defaultClassLoader);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
