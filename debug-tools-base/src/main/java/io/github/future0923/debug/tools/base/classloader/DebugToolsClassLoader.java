package io.github.future0923.debug.tools.base.classloader;

import io.github.future0923.debug.tools.base.logging.Logger;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author future0923
 */
public class DebugToolsClassLoader extends URLClassLoader {

    private static final Logger logger = Logger.getLogger(DebugToolsClassLoader.class);

    public DebugToolsClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        logger.debug("DebugToolsClassLoader parent is " + parent);
    }

    @Override
    public synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
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
            try (JarFile jarFile = new JarFile(url.getPath())) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.endsWith(".class")
                            && jarEntryName.startsWith("io/github/future0923/debug/tools/")
                            && !jarEntryName.startsWith("io/github/future0923/debug/tools/server/mock/")) {
                        String className = jarEntryName.replace("/", ".").substring(0, jarEntryName.length() - 6);
                        try {
                            loadClass(className);
                        } catch (ClassNotFoundException ignored) {

                        }
                    }
                }
            } catch (Exception ignored) {

            }
        }
    }
}