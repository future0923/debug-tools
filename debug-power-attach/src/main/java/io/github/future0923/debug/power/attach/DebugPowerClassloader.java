package io.github.future0923.debug.power.attach;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author future0923
 */
public class DebugPowerClassloader extends URLClassLoader {
    public DebugPowerClassloader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
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

    public void loadAllClasses() {
        for (URL url : getURLs()) {
            try(JarFile jarFile = new JarFile(url.getPath())) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    if (jarEntry.getName().endsWith(".class")) {
                        String className = jarEntry.getName().replace("/", ".").substring(0, jarEntry.getName().length() - 6);
                        loadClass(className);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}