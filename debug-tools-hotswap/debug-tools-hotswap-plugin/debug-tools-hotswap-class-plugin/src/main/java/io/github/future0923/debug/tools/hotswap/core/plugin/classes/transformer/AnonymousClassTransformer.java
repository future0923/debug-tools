package io.github.future0923.debug.tools.hotswap.core.plugin.classes.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassMap;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.dto.AnonymousClassInfo;
import io.github.future0923.debug.tools.hotswap.core.plugin.classes.dto.AnonymousClassInfos;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author future0923
 */
public class AnonymousClassTransformer {

    private static final Logger logger = Logger.getLogger(AnonymousClassTransformer.class);

    private static final Map<ClassLoader, Map<String, AnonymousClassInfos>> anonymousClassInfosMap = new WeakHashMap<ClassLoader, Map<String, AnonymousClassInfos>>();

    @Init
    static HotswapTransformer hotswapTransformer;

    @OnClassLoadEvent(classNameRegexp = ".*\\$\\d+", events = LoadEvent.REDEFINE)
    public static CtClass patchAnonymousClass(ClassLoader classLoader, ClassPool classPool, String className, Class original)
            throws IOException, NotFoundException, CannotCompileException {
        String javaClass = className.replaceAll("/", ".");
        String mainClass = javaClass.replaceAll("\\$\\d+$", "");
        // 合成类不要
        if (classPool.find(className) == null) {
            return null;
        }
        AnonymousClassInfos info = getStateInfo(classLoader, classPool, mainClass);
        String compatibleName = info.getCompatibleTransition(javaClass);
        if (compatibleName != null) {
            logger.debug("Anonymous class '{}' - replacing with class file {}.", javaClass, compatibleName);
            CtClass ctClass = classPool.get(compatibleName);
            ctClass.replaceClassName(compatibleName, javaClass);
            return ctClass;
        } else {
            logger.debug("Anonymous class '{}' - not compatible change is replaced with empty implementation.", javaClass, compatibleName);
            CtClass ctClass = classPool.makeClass(javaClass);
            ctClass.setSuperclass(classPool.get(original.getSuperclass().getName()));
            Class<?>[] originalInterfaces = original.getInterfaces();
            CtClass[] interfaces = new CtClass[originalInterfaces.length];
            for (int i = 0; i < originalInterfaces.length; i++)
                interfaces[i] = classPool.get(originalInterfaces[i].getName());
            ctClass.setInterfaces(interfaces);
            return ctClass;
        }
    }

    private static boolean isHotswapAgentSyntheticClass(String compatibleName) {
        String anonymousClassIndexString = compatibleName.replaceAll("^.*\\$(\\d+)$", "$1");
        try {
            long anonymousClassIndex = Long.parseLong(anonymousClassIndexString);
            return anonymousClassIndex >= AnonymousClassInfos.UNIQUE_CLASS_START_INDEX;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(compatibleName + " is not in a format of className$i");
        }
    }

    private static void registerReplaceOnLoad(final String newName, final CtClass anonymous) {
        hotswapTransformer.registerTransformer(null, newName, new HaClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                logger.trace("Anonymous class '{}' - replaced.", newName);
                hotswapTransformer.removeTransformer(newName, this);
                try {
                    return anonymous.toBytecode();
                } catch (Exception e) {
                    logger.error("Unable to create bytecode of class {}.", e, anonymous.getName());
                    return null;
                }
            }
            @Override
            public boolean isForRedefinitionOnly() {
                return false;
            }
        });
    }

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static byte[] patchMainClass(String className, ClassPool classPool, CtClass ctClass,
                                        ClassLoader classLoader, ProtectionDomain protectionDomain) throws IOException, CannotCompileException, NotFoundException {
        String javaClassName = className.replaceAll("/", ".");
        if (!ClassLoaderHelper.isClassLoaded(classLoader, javaClassName + "$1")) {
            return null;
        }
        AnonymousClassInfos stateInfo = getStateInfo(classLoader, classPool, javaClassName);
        Map<AnonymousClassInfo, AnonymousClassInfo> transitions = stateInfo.getCompatibleTransitions();
        ClassMap replaceClassNameMap = new ClassMap();
        for (Map.Entry<AnonymousClassInfo, AnonymousClassInfo> entry : transitions.entrySet()) {
            String compatibleName = entry.getKey().getClassName();
            String newName = entry.getValue().getClassName();
            if (!newName.equals(compatibleName)) {
                replaceClassNameMap.put(newName, compatibleName);
                logger.trace("Class '{}' replacing '{}' for '{}'", javaClassName, newName, compatibleName);
            }
            if (isHotswapAgentSyntheticClass(compatibleName)) {
                logger.debug("Anonymous class '{}' not comatible and is replaced with synthetic class '{}'", newName, compatibleName);
                CtClass anonymous = classPool.get(newName);
                anonymous.replaceClassName(newName, compatibleName);
                anonymous.toClass(classLoader, protectionDomain);
            } else if (!ClassLoaderHelper.isClassLoaded(classLoader, newName)) {
                CtClass anonymous = classPool.get(compatibleName);
                anonymous.replaceClassName(compatibleName, newName);
                logger.debug("Anonymous class '{}' - will be replaced from class file {}.", newName, compatibleName);
                registerReplaceOnLoad(newName, anonymous);
            }
        }
        ctClass.replaceClassName(replaceClassNameMap);
        logger.reload("Class '{}' has been enhanced with anonymous classes for hotswap.", className);
        return ctClass.toBytecode();
    }

    private static synchronized AnonymousClassInfos getStateInfo(ClassLoader classLoader, ClassPool classPool, String className) {
        Map<String, AnonymousClassInfos> classInfosMap = getClassInfosMapForClassLoader(classLoader);

        AnonymousClassInfos infos = classInfosMap.get(className);

        if (infos == null || !infos.isCurrent(classPool)) {
            if (infos == null) {
                logger.trace("Creating new infos for className {}", className);
            } else {
                logger.trace("Creating new infos, current is obsolete for className {}", className);
            }
            infos = new AnonymousClassInfos(classPool, className);
            infos.mapPreviousState(new AnonymousClassInfos(classLoader, className));
            classInfosMap.put(className, infos);
        } else {
            logger.trace("Returning existing infos for className {}", className);
        }
        return infos;
    }

    private static Map<String, AnonymousClassInfos> getClassInfosMapForClassLoader(final ClassLoader classLoader) {
        Map<String, AnonymousClassInfos> classInfosMap = anonymousClassInfosMap.get(classLoader);
        if (classInfosMap == null) {
            synchronized (classLoader) {
                classInfosMap = anonymousClassInfosMap.computeIfAbsent(classLoader, k -> new HashMap<>());
            }
        }
        return classInfosMap;
    }
}
