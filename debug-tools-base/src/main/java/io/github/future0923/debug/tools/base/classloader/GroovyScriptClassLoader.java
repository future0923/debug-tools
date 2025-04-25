package io.github.future0923.debug.tools.base.classloader;

/**
 * Groovy运行的父类，用来兼容应用程序的类和DebugTools程序的类的加载
 *
 * @author future0923
 */
public class GroovyScriptClassLoader extends ClassLoader {

    private ClassLoaderWrapper defaultClassLoader;

    private static GroovyScriptClassLoader classLoader;

    public static GroovyScriptClassLoader getInstance() {
        return classLoader;
    }

    public static GroovyScriptClassLoader init(ClassLoader debugToolsClassLoader) {
        GroovyScriptClassLoader.classLoader = new GroovyScriptClassLoader(debugToolsClassLoader);
        return getInstance();
    }

    private GroovyScriptClassLoader(ClassLoader debugToolsClassLoader) {
        super(debugToolsClassLoader);
    }

    public void setDefaultClassLoader(ClassLoader defaultClassLoader) {
        this.defaultClassLoader = new ClassLoaderWrapper(defaultClassLoader);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException e) {
            return defaultClassLoader.loadClass(name, resolve);
        }
    }
}
