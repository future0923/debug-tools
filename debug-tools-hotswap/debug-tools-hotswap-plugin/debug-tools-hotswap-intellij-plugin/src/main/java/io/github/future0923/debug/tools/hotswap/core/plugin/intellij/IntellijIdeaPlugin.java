/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.intellij;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

/**
 * intelliJ idea 在 java8 的时候可以开启 shorten command line 选择 classpath file 模式。
 * 这时候通过 {@code com.intellij.rt.execution.CommandLineWrapper} 加载。
 * 使用的是 {@link java.net.URLClassLoader}，而 agent 采用的是 {@code AppClassLoader}，导致加载不到 agent 中的 class 。
 *
 * @author future0923
 */
@Plugin(name = "IntelliJIdea",
        description = "IntelliJ Idea plugin",
        testedVersions = {"2025.2"},
        expectedVersions = {"all"}
)
public class IntellijIdeaPlugin {

    private static final Logger logger = Logger.getLogger(IntellijIdeaPlugin.class);

    private static boolean initialized = false;

    public void init() {
        if (!initialized) {
            initialized = true;
            logger.info("patch intellij idea success");
        }
    }

    @OnClassLoadEvent(classNameRegexp = "com.intellij.rt.execution.CommandLineWrapper")
    public static void patchCommandLineWrapper(CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtMethod loadMainClassWithCustomLoader = ctClass.getDeclaredMethod("loadMainClassWithCustomLoader");
        loadMainClassWithCustomLoader.instrument(new ExprEditor() {
            @Override
            public void edit(NewExpr e) throws CannotCompileException {
                if (e.getClassName().equals("java.net.URLClassLoader")) {
                    // 把“new URLClassLoader(原$1, (ClassLoader) null)”替换为
                    //  “new URLClassLoader(原$1, CommandLineWrapper.class.getClassLoader())”
                    e.replace("{ $_ = io.github.future0923.debug.tools.hotswap.core.util.classloader.URLClassLoaderPathHelper.prependClassPath(com.intellij.rt.execution.CommandLineWrapper.class.getClassLoader(), $1); }");
                }
            }
        });
        logger.info("patch intellij idea CommandLineWrapper success");
    }

    @OnClassLoadEvent(classNameRegexp = "com.intellij.util.lang.UrlClassLoader")
    public static void patchUrlClassLoader(CtClass ctClass) throws CannotCompileException {
        if (!initialized) {
            String initializePlugin = PluginManagerInvoker.buildInitializePlugin(IntellijIdeaPlugin.class, "appClassLoader");
            String initializeThis = PluginManagerInvoker.buildCallPluginMethod("appClassLoader", IntellijIdeaPlugin.class, "init");
            for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
                constructor.insertAfter(initializePlugin);
                constructor.insertAfter(initializeThis);
            }
        }

        try {
            CtMethod findClass = ctClass.getDeclaredMethod("findClass");
            findClass.insertBefore("{" +
                    "   if ($1.startsWith(" + ProjectConstants.PROJECT_PACKAGE_PREFIX + ")) { " +
                    "       return appClassLoader.loadClass($1);" +
                    "   }" +
                    "}"
            );
        } catch (NotFoundException e) {
            logger.error("Unable to find method \"findClass()\" in com.intellij.util.lang.UrlClassLoader.", e);
        }

        try {
            CtMethod getResourceAsStream = ctClass.getDeclaredMethod("getResourceAsStream");
            getResourceAsStream.insertBefore("{" +
                    "   if ($1.startsWith(" + ProjectConstants.PROJECT_PACKAGE_PATH + ")) { " +
                    "       return appClassLoader.getResourceAsStream($1);" +
                    "   }" +
                    "}"
            );
        } catch (NotFoundException e) {
            logger.error("Unable to find method \"getResourceAsStream()\" in com.intellij.util.lang.UrlClassLoader.", e);
        }
        ctClass.addMethod(CtNewMethod.make(
                "public java.net.URL getResource(String name) {" +
                    "   if (name.startsWith(" + ProjectConstants.PROJECT_PACKAGE_PATH + ")) { " +
                    "       return appClassLoader.getResource(name);" +
                    "   }" +
                    "   return super.getResource(name);" +
                    "}", ctClass)
        );
        logger.info("patch intellij idea UrlClassLoader success");
    }
}
