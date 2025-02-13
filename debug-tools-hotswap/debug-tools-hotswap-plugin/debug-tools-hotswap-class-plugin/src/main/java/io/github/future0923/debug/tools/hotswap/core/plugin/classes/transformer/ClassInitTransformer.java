package io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 热重载 static 变量、static final 变量、static 代码块、枚举值
 *
 * @author future0923
 */
public class ClassInitTransformer {

    private static final Logger logger = Logger.getLogger(ClassInitTransformer.class);

    private static final String HOTSWAP_AGENT_CLINIT_METHOD = "$$ha$clinit";

    @Init
    static Scheduler scheduler;

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void patchClassInit(final CtClass ctClass, final ClassLoader classLoader, final Class<?> originalClass) throws IOException, CannotCompileException, NotFoundException {
        if (isSyntheticClass(originalClass)) {
            return;
        }
        final String className = ctClass.getName();
        try {
            CtMethod origMethod = ctClass.getDeclaredMethod(HOTSWAP_AGENT_CLINIT_METHOD);
            ctClass.removeMethod(origMethod);
        } catch (NotFoundException ignored) {

        }
        CtConstructor clinit = ctClass.getClassInitializer();
        if (clinit != null) {
            CtConstructor haClinit = new CtConstructor(clinit, ctClass, null);
            haClinit.getMethodInfo().setName(HOTSWAP_AGENT_CLINIT_METHOD);
            haClinit.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
            ctClass.addConstructor(haClinit);
        }
        scheduler.scheduleCommand(() -> {
            try {
                Class<?> clazz = classLoader.loadClass(className);
                Method m = clazz.getDeclaredMethod(HOTSWAP_AGENT_CLINIT_METHOD);
                m.setAccessible(true);
                m.invoke(null);
                logger.reload("Reload static info for class {}", className);
            } catch (Exception e) {
                logger.debug("Error initializing redefined class {}", e, className);
            }
        }, 150);
    }

    private static boolean isSyntheticClass(Class<?> classBeingRedefined) {
        return classBeingRedefined.getSimpleName().contains("$$_javassist")
                || classBeingRedefined.getSimpleName().contains("$$_jvst")
                || classBeingRedefined.getName().startsWith("com.sun.proxy.$Proxy")
                || classBeingRedefined.getSimpleName().contains("$$");
    }
}
