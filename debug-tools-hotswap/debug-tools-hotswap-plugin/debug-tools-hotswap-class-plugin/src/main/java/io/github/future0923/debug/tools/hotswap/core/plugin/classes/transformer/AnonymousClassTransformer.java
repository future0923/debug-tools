/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    /**
     * 用兼容的更改替换匿名类（根据状态信息从另一个类中获取）。
     * 如果没有兼容的类，则使用兼容的空实现来替换。
     */
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
            logger.info("Anonymous class '{}' - replacing with class file {}.", javaClass, compatibleName);
            CtClass ctClass = classPool.get(compatibleName);
            ctClass.replaceClassName(compatibleName, javaClass);
            return ctClass;
        } else {
            logger.info("Anonymous class '{}' - not compatible change is replaced with empty implementation.", javaClass, compatibleName);
            CtClass ctClass = classPool.makeClass(javaClass);
            ctClass.setSuperclass(classPool.get(original.getSuperclass().getName()));
            Class<?>[] originalInterfaces = original.getInterfaces();
            CtClass[] interfaces = new CtClass[originalInterfaces.length];
            for (int i = 0; i < originalInterfaces.length; i++) {
                interfaces[i] = classPool.get(originalInterfaces[i].getName());
            }
            ctClass.setInterfaces(interfaces);
            return ctClass;
        }
    }

    /**
     * 如果类包含匿名类，则将类引用重命名为兼容的过渡类。
     * 如果转换后的类没有通过热重载替换加载，则需要捕获类define事件来进行替换。
     * 为不兼容的更改定义新的合成类。
     */
    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static byte[] patchMainClass(String className, ClassPool classPool, CtClass ctClass,
                                        ClassLoader classLoader, ProtectionDomain protectionDomain) throws IOException, CannotCompileException, NotFoundException {
        String javaClassName = className.replaceAll("/", ".");
        // 是否有匿名类
        if (!ClassLoaderHelper.isClassLoaded(classLoader, javaClassName + "$1")) {
            // 没有返回null不修改
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


    /**
     * 是否是热重载生成的合成类
     * @param compatibleName 兼容类名称
     */
    private static boolean isHotswapAgentSyntheticClass(String compatibleName) {
        String anonymousClassIndexString = compatibleName.replaceAll("^.*\\$(\\d+)$", "$1");
        try {
            long anonymousClassIndex = Long.parseLong(anonymousClassIndexString);
            return anonymousClassIndex >= AnonymousClassInfos.UNIQUE_CLASS_START_INDEX;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(compatibleName + " is not in a format of className$i");
        }
    }

    /**
     * 新建的匿名类不受热重载({@link #patchAnonymousClass(ClassLoader, ClassPool, String, Class)})影响，注册自定义的 transformer，transform时转换并注销该transformer
     */
    private static void registerReplaceOnLoad(final String newName, final CtClass anonymous) {
        hotswapTransformer.registerTransformer(null, newName, new HaClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                logger.info("Anonymous class '{}' - replaced.", newName);
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

    /**
     * 从当前 ClassLoader/filesystem 中计算匿名类的 new/previous 状态信息。
     * 通过主类文件上的修改时间来判断状态信息是否是最新。
     */
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

    /**
     * 获取类加载器的类信息
     *
     * @param classLoader 类加载器
     * @return 类加载器中的类信息
     */
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
