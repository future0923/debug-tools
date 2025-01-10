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
package io.github.future0923.debug.tools.hotswap.core.util;

import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderDefineClassPatcher;

import java.lang.reflect.Method;

/**
 * 提供在其他ClassLoader中调用{@link PluginManager}的能力，避免类加载器冲突。真正的插件在AgentClassLoader中，其他插件通过{@link ClassLoaderDefineClassPatcher#patch}过去的
 */
public class PluginManagerInvoker {

    /**
     * 构建初始化插件javassist字符串
     *
     * @param pluginClass 插件类
     */
    public static String buildInitializePlugin(Class<?> pluginClass) {
        return buildInitializePlugin(pluginClass, "getClass().getClassLoader()");
    }

    /**
     * 构建初始化插件javassist字符串
     *
     * @param pluginClass 插件类
     * @param classLoader 在哪个类加载器中初始化
     */
    public static String buildInitializePlugin(Class<?> pluginClass, String classLoader) {
        return "io.github.future0923.debug.tools.hotswap.core.config.PluginManager.getInstance().getPluginRegistry().initializePlugin(" +
                "\"" + pluginClass.getName() + "\", " + classLoader +
                ");";
    }

    /**
     * 在指定类加载器中初始化插件
     */
    @SuppressWarnings("unchecked")
    public static <T> T callInitializePlugin(Class<T> pluginClass, ClassLoader appClassLoader) {
        return (T) PluginManager.getInstance().getPluginRegistry().initializePlugin(
                pluginClass.getName(), appClassLoader
        );
    }

    /**
     * 从指定类加载器中移除热重载插件
     */
    public static void callCloseClassLoader(ClassLoader classLoader) {
        PluginManager.getInstance().closeClassLoader(classLoader);
    }

    /**
     * 构建从指定类加载器中移除热重载插件javassist字符串
     */
    public static String buildCallCloseClassLoader(String classLoader) {
        return "io.github.future0923.debug.tools.hotswap.core.config.PluginManager.getInstance().closeClassLoader(" + classLoader + ");";
    }

    /**
     * 反射调用指定ClassLoader中的Plugin方法
     *
     * @return 方法的返回值
     */
    public static Object callPluginMethod(Class<?> pluginClass, ClassLoader appClassLoader, String method, Class<?>[] paramTypes, Object[] params) {
        Object pluginInstance = PluginManager.getInstance().getPlugin(pluginClass.getName(), appClassLoader);
        try {
            Method m = pluginInstance.getClass().getDeclaredMethod(method, paramTypes);
            return m.invoke(pluginInstance, params);
        } catch (Exception e) {
            throw new Error(String.format("Exception calling method %s on plugin class %s", method, pluginClass), e);
        }
    }

    /**
     * 构建反射调用的指定ClassLoader中的Plugin方法的javassist字符串
     *
     * @param paramValueAndType 值 类型 值 类型...
     */
    public static String buildCallPluginMethod(Class<?> pluginClass, String method, String... paramValueAndType) {
        return buildCallPluginMethod("getClass().getClassLoader()", pluginClass, method, paramValueAndType);
    }

    /**
     * 构建反射调用的指定ClassLoader中的Plugin方法的javassist字符串
     */
    public static String buildCallPluginMethod(String appClassLoaderVar, Class<?> pluginClass,
                                               String method, String... paramValueAndType) {

        String managerClass = PluginManager.class.getName();
        int paramCount = paramValueAndType.length / 2;

        StringBuilder b = new StringBuilder();

        b.append("try {");

        b.append("ClassLoader __pluginClassLoader = ");
        b.append(managerClass);
        b.append(".class.getClassLoader();");

        // Object __pluginInstance = io.github.future0923.debug.tools.hotswap.core.config.PluginManager.getInstance().getPlugin(io.github.future0923.debug.tools.hotswap.core.plugin.TestPlugin.class.getName(), __pluginClassLoader);
        b.append("Object __pluginInstance = ");
        b.append(managerClass);
        b.append(".getInstance().getPlugin(");
        b.append(pluginClass.getName());
        b.append(".class.getName(), ").append(appClassLoaderVar).append(");");

        // Class __pluginClass = __pluginClassLoader.loadClass("io.github.future0923.debug.tools.hotswap.core.plugin.TestPlugin");
        b.append("Class __pluginClass = ");
        b.append("__pluginClassLoader.loadClass(\"");
        b.append(pluginClass.getName());
        b.append("\");");

        // param types
        b.append("Class[] paramTypes = new Class[").append(paramCount).append("];");
        for (int i = 0; i < paramCount; i++) {
            // paramTypes[i] = = __pluginClassLoader.loadClass("my.test.TestClass").getClass();
            b.append("paramTypes[").append(i).append("] = __pluginClassLoader.loadClass(\"").append(paramValueAndType[(i * 2) + 1]).append("\");");
        }

        //   java.lang.reflect.Method __pluginMethod = __pluginClass.getDeclaredMethod("method", paramType1, paramType2);
        b.append("java.lang.reflect.Method __callPlugin = __pluginClass.getDeclaredMethod(\"");
        b.append(method);
        b.append("\", paramTypes");
        b.append(");");

        b.append("Object[] params = new Object[").append(paramCount).append("];");
        for (int i = 0; i < paramCount; i = i + 1) {
            b.append("params[").append(i).append("] = ").append(paramValueAndType[i * 2]).append(";");
        }

        // __pluginMethod.invoke(__pluginInstance, param1, param2);
        b.append("__callPlugin.invoke(__pluginInstance, params);");

        // catch (Exception e) {throw new Error(e);}
        b.append("} catch (Exception e) {throw new Error(e);}");

        return b.toString();
    }
}
