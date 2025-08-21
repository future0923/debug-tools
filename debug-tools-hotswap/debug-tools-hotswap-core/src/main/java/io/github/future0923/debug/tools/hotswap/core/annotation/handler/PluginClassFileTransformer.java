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
package io.github.future0923.debug.tools.hotswap.core.annotation.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.JavassistUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

import java.io.IOException;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 插件类文件Transformer，当class transform时反射调用加了{@link OnClassLoadEvent}注解的方法
 */
public class PluginClassFileTransformer implements HaClassFileTransformer {

    protected static final Logger LOGGER = Logger.getLogger(PluginClassFileTransformer.class);

    /**
     * 插件上的注解信息
     */
    private final PluginAnnotation<OnClassLoadEvent> pluginAnnotation;

    /**
     * 插件类感兴趣的事件
     */
    private final List<LoadEvent> events;

    /**
     * 插件管理器
     */
    private final PluginManager pluginManager;

    public PluginClassFileTransformer(PluginManager pluginManager, PluginAnnotation<OnClassLoadEvent> pluginAnnotation) {
        this.pluginManager = pluginManager;
        this.pluginAnnotation = pluginAnnotation;
        this.events = Arrays.asList(pluginAnnotation.getAnnotation().events());
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return !events.contains(LoadEvent.DEFINE);
    }

    public boolean isPluginDisabled(ClassLoader loader){
        if(loader != null && pluginManager != null && pluginManager.getPluginConfiguration(loader) != null) {
            return pluginManager.getPluginConfiguration(loader).isDisabledPlugin(pluginAnnotation.getPluginClass());
        }
        return false;
    }

    public boolean isFallbackPlugin(){
        return pluginAnnotation.isFallBack();
    }

    public String getPluginGroup() {
        return pluginAnnotation.getGroup();
    }

    public boolean versionMatches(ClassLoader loader){
        return true;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if ((classBeingRedefined == null) ? !events.contains(LoadEvent.DEFINE) : !events.contains(LoadEvent.REDEFINE)) {
            LOGGER.trace("Not a handled event!", events);
            return classfileBuffer;
        }

        if (pluginManager.getPluginConfiguration(loader).isDisabledPlugin(pluginAnnotation.getPluginClass())) {
            LOGGER.trace("Plugin NOT enabled! {}", pluginAnnotation);
            return classfileBuffer;
        }

        return transform(pluginManager, pluginAnnotation, loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }

    /**
     * 判断是否是合成类，跳过 proxy 和 javassist synthetic classes 类
     */
    protected static boolean isSyntheticClass(String className) {
        return className.contains("$$_javassist")
                || className.contains("$$_jvst")
                || className.startsWith("com/sun/proxy")
                || (className.startsWith("jdk/proxy") && className.contains("$Proxy"))
                ;
    }

    /**
     * 反射调用{@link OnClassLoadEvent}
     */
    public static byte[] transform(PluginManager pluginManager, PluginAnnotation<OnClassLoadEvent> pluginAnnotation, ClassLoader classLoader, String className, Class<?> redefiningClass, ProtectionDomain protectionDomain, byte[] bytes) {
        LOGGER.trace("Transforming.... '{}' using: '{}'", className, pluginAnnotation);

        if (pluginAnnotation.getAnnotation().skipSynthetic()) {
            if (isSyntheticClass(className) || (redefiningClass != null && redefiningClass.isSynthetic())) {
                return bytes;
            }
        }

        if (pluginAnnotation.getAnnotation().skipAnonymous()) {
            if (className.matches("\\$\\d+$")) {
                return bytes;
            }
        }

        if (classLoader != null) {
            pluginManager.initClassLoader(classLoader, protectionDomain);
        }

        // default result
        byte[] result = bytes;

        CtClass ctClass = null;

        List<Object> args = new ArrayList<>();
        for (Class<?> type : pluginAnnotation.getMethod().getParameterTypes()) {
            if (type.isAssignableFrom(ClassLoader.class)) {
                args.add(classLoader);
            } else if (type.isAssignableFrom(String.class)) {
                args.add(className);
            } else if (type.isAssignableFrom(Class.class)) {
                args.add(redefiningClass);
            } else if (type.isAssignableFrom(ProtectionDomain.class)) {
                args.add(protectionDomain);
            } else if (type.isAssignableFrom(byte[].class)) {
                args.add(bytes);
            } else if (type.isAssignableFrom(ClassPool.class)) {
                args.add(JavassistUtil.getClassPool(classLoader));
            } else if (type.isAssignableFrom(CtClass.class)) {
                try {
                    ctClass = JavassistUtil.createCtClass(classLoader, bytes);
                    args.add(ctClass);
                } catch (IOException e) {
                    LOGGER.error("Unable create CtClass for '" + className + "'.", e);
                    return result;
                }
            } else if (type.isAssignableFrom(LoadEvent.class)) {
                args.add(redefiningClass == null ? LoadEvent.DEFINE : LoadEvent.REDEFINE);
            } else {
                LOGGER.error("Unable to call init method on plugin '" + pluginAnnotation.getPluginClass() + "'." + " Method parameter type '" + type + "' is not recognized for @Init annotation.");
                return result;
            }
        }
        try {
            Object resultObject = pluginAnnotation.getMethod().invoke(pluginAnnotation.getPlugin(), args.toArray());
            if (resultObject == null) {

            } else if (resultObject instanceof byte[]) {
                result = (byte[]) resultObject;
            } else if (resultObject instanceof CtClass) {
                result = ((CtClass) resultObject).toBytecode();
                if (resultObject != ctClass) {
                    ((CtClass) resultObject).detach();
                }
            } else {
                LOGGER.error("Unknown result of @OnClassLoadEvent method '" + result.getClass().getName() + "'.");
            }
            if (ctClass != null) {
                if (resultObject == null) {
                    result = ctClass.toBytecode();
                }
                ctClass.detach();
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException in transform method on plugin '{}' class '{}' of classLoader '{}'",
                e, pluginAnnotation.getPluginClass(), className,
                classLoader != null ? classLoader.getClass().getName() : "");
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException in transform method on plugin '{}' class '{}' of classLoader '{}'",
                e, pluginAnnotation.getPluginClass(), className,
                classLoader != null ? classLoader.getClass().getName() : "");
        } catch (CannotCompileException e) {
            LOGGER.error("Cannot compile class after manipulation on plugin '{}' class '{}' of classLoader '{}'",
                e, pluginAnnotation.getPluginClass(), className,
                classLoader != null ? classLoader.getClass().getName() : "");
        } catch (IOException e) {
            LOGGER.error("IOException in transform method on plugin '{}' class '{}' of classLoader '{}'",
                e, pluginAnnotation.getPluginClass(), className,
                classLoader != null ? classLoader.getClass().getName() : "");
        }

        return result;
    }

    @Override
    public String toString() {
        return "\n\t\t\tPluginClassFileTransformer [pluginAnnotation=" + pluginAnnotation + "]";
    }

}
