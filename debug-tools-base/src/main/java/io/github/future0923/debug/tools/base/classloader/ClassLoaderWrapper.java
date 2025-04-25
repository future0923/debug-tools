package io.github.future0923.debug.tools.base.classloader;

/**
 * 类加载器包装器，主要是可以调用父类的方法
 *
 * @author future0923
 */
public class ClassLoaderWrapper extends ClassLoader {

    public ClassLoaderWrapper(ClassLoader defaultClassLoader) {
        super(defaultClassLoader);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
