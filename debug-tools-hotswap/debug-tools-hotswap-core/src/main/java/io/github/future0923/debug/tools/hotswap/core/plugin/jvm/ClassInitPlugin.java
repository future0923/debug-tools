/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.jvm;

import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.Modifier;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.ExprEditor;
import io.github.future0923.debug.tools.hotswap.core.javassist.expr.FieldAccess;
import io.github.future0923.debug.tools.base.logging.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ClassInitPlugin在类重新定义后初始化静态变量的新枚举值
 */
@Plugin(name = "ClassInitPlugin",
        description = "Initialize empty static fields (left by DCEVM) using code from <clinit> method.",
        testedVersions = {"DCEVM"})
public class ClassInitPlugin {

    private static final Logger LOGGER = Logger.getLogger(ClassInitPlugin.class);

    private static final String HOTSWAP_AGENT_CLINIT_METHOD = "$$ha$clinit";

    public static boolean reloadFlag;

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void patch(final CtClass ctClass, final ClassLoader classLoader, final Class<?> originalClass) throws IOException, CannotCompileException, NotFoundException {

        if (isSyntheticClass(originalClass)) {
            return;
        }

        final String className = ctClass.getName();

        try {
            CtMethod origMethod = ctClass.getDeclaredMethod(HOTSWAP_AGENT_CLINIT_METHOD);
            ctClass.removeMethod(origMethod);
        } catch (NotFoundException ex) {
            // swallow
        }

        CtConstructor clinit = ctClass.getClassInitializer();

        if (clinit != null) {
            LOGGER.debug("Adding " + HOTSWAP_AGENT_CLINIT_METHOD + " to class: {}", className);
            CtConstructor haClinit = new CtConstructor(clinit, ctClass, null);
            haClinit.getMethodInfo().setName(HOTSWAP_AGENT_CLINIT_METHOD);
            haClinit.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
            ctClass.addConstructor(haClinit);

            final boolean[] reinitializeStatics = new boolean[] { false };

            haClinit.instrument(
                new ExprEditor() {
                    public void edit(FieldAccess f) throws CannotCompileException {
                        try {
                            if (f.isStatic() && f.isWriter()) {
                                Field originalField = null;
                                try {
                                    originalField = originalClass.getDeclaredField(f.getFieldName());
                                } catch (NoSuchFieldException e) {
                                    LOGGER.debug("New field will be initialized {}", f.getFieldName());
                                    reinitializeStatics[0] = true;
                                }
                                if (originalField != null) {
                                    // Enum class contains an array field, in javac it's name starts with $VALUES,
                                    // in eclipse compiler starts with ENUM$VALUES
                                    if (originalClass.isEnum() && f.getSignature().startsWith("[L")
                                            && (f.getFieldName().startsWith("$VALUES")
                                                || f.getFieldName().startsWith("ENUM$VALUES"))) {
                                        if (reinitializeStatics[0]) {
                                            LOGGER.debug("New field will be initialized {}", f.getFieldName());
                                        } else {
                                            reinitializeStatics[0] = checkOldEnumValues(ctClass, originalClass);
                                        }
                                    } else {
                                        LOGGER.debug("Skipping old field {}", f.getFieldName());
                                        f.replace("{}");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Patching " + HOTSWAP_AGENT_CLINIT_METHOD + " method failed.", e);
                        }
                    }

                }
            );

            if (reinitializeStatics[0]) {
                PluginManager.getInstance().getScheduler().scheduleCommand(new Command() {
                    @Override
                    public void executeCommand() {
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            Method m = clazz.getDeclaredMethod(HOTSWAP_AGENT_CLINIT_METHOD, new Class[] {});
                            m.setAccessible(true);
                            m.invoke(null, new Object[] {});
                            LOGGER.debug("Initializer {} invoked for class {}", HOTSWAP_AGENT_CLINIT_METHOD, className);
                        } catch (Exception e) {
                            LOGGER.error("Error initializing redefined class {}", e, className);
                        } finally {
                            reloadFlag = false;
                        }
                    }
                }, 150); // Hack : init must be called after dependant class redefinition. Since the class can
                         // be proxied, the class init must be scheduled after proxy redefinition. Currently proxy
                         // redefinition (in ProxyPlugin) is scheduled with 100ms delay, therefore we use delay 150ms.
            } else {
                reloadFlag = false;
            }
        }
    }

    private static boolean checkOldEnumValues(CtClass ctClass, Class<?> originalClass) {
        if (ctClass.isEnum()) {
            // Check if some field from original enumeration was deleted
            Enum<?>[] enumConstants = (Enum<?>[]) originalClass.getEnumConstants();
            for (Enum<?> en : enumConstants) {
                try {
                    CtField existing = ctClass.getDeclaredField(en.toString());
                } catch (NotFoundException e) {
                    LOGGER.debug("Enum field deleted. $VALUES will be reinitialized {}", en.toString());
                    return true;
                }
            }
        } else {
            LOGGER.error("Patching " + HOTSWAP_AGENT_CLINIT_METHOD + " method failed. Enum type expected {}", ctClass.getName());
        }
        return false;
    }

    private static boolean isSyntheticClass(Class<?> classBeingRedefined) {
        return classBeingRedefined.getSimpleName().contains("$$_javassist")
                || classBeingRedefined.getSimpleName().contains("$$_jvst")
                || classBeingRedefined.getName().startsWith("com.sun.proxy.$Proxy")
                || classBeingRedefined.getSimpleName().contains("$$");
    }

}
