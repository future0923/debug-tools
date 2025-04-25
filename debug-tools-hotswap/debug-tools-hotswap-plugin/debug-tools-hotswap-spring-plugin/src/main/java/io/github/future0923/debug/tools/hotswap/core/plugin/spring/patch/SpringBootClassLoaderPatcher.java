package io.github.future0923.debug.tools.hotswap.core.plugin.spring.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * @author future0923
 */
public class SpringBootClassLoaderPatcher {

    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.DefaultListableBeanFactory")
    public static void patchDefaultListableBeanFactory(CtClass clazz) throws NotFoundException, CannotCompileException {
        CtMethod method = clazz.getDeclaredMethod("preInstantiateSingletons");
        String body = "{" +
                "    try {" +
                "            java.lang.ClassLoader classLoader = org.springframework.beans.factory.support.DefaultListableBeanFactory.class.getClassLoader();" +
                "            java.lang.Class pluginManager = classLoader.loadClass(\"" + PluginManager.class.getName() + "\");" +
                "            java.lang.reflect.Method enhanceClassLoader = pluginManager.getDeclaredMethod(\"enhanceClassLoader\", new java.lang.Class[] { java.lang.ClassLoader.class });" +
                "            enhanceClassLoader.setAccessible(true);" +
                "            enhanceClassLoader.invoke(null, new java.lang.Object[] { classLoader });" +
                "    } catch (java.lang.Exception e) {" +
                "            throw new java.lang.RuntimeException(e);" +
                "    }" +
                "}";
        method.insertAfter(body);
    }
}
