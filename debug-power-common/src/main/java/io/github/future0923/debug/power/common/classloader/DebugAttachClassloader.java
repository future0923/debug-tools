package io.github.future0923.debug.power.common.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author future0923
 */
public class DebugAttachClassloader extends URLClassLoader {
    public DebugAttachClassloader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent（SystemClassLoader）里加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("sun.") || name.startsWith("java."))) {
            return super.loadClass(name, resolve);
        }
        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            // ignore
        }
        return super.loadClass(name, resolve);
    }
}