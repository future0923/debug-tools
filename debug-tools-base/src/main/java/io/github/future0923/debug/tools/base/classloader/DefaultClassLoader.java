package io.github.future0923.debug.tools.base.classloader;

import io.github.future0923.debug.tools.base.SpyAPI;
import io.github.future0923.debug.tools.base.utils.DebugToolsJvmUtils;
import lombok.Getter;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认的类加载器，主要是loadClass方法变成public
 *
 * @author future0923
 */
public class DefaultClassLoader extends ClassLoader {

    public static final List<String> baseClassList = new ArrayList<>();

    @Getter
    private static volatile ClassLoader defaultClassLoader;

    static {
        baseClassList.add("org.slf4j.Logger");
        baseClassList.add("org.apache.log4j.Logger");
        baseClassList.add("org.springframework.beans.factory.BeanFactory");
    }

    public DefaultClassLoader(ClassLoader defaultClassLoader) {
        super(defaultClassLoader);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    public static ClassLoader getDefaultClassLoader(Instrumentation instrumentation) {
        if (defaultClassLoader == null) {
            synchronized (DefaultClassLoader.class) {
                if (defaultClassLoader == null) {
                    String mainClass = DebugToolsJvmUtils.getMainClass();
                    for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                        ClassLoader classLoader = clazz.getClassLoader();
                        if (defaultClassLoader == null) {
                            if (mainClass != null) {
                                if (mainClass.equals(clazz.getName())) {
                                    defaultClassLoader = classLoader;
                                    break;
                                }
                            } else if (baseClassList.contains(clazz.getName())) {
                                defaultClassLoader = classLoader;
                                break;
                            }
                        }
                    }
                    if (defaultClassLoader == null) {
                        defaultClassLoader = SpyAPI.class.getClassLoader();
                    }
                }
            }
        }
        return defaultClassLoader;
    }
}
