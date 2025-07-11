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
package io.github.future0923.debug.tools.hotswap.core.plugin.fastjson;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 支持fastjson 1+、fastjson 2+、fastjson2版本
 *
 * @author future0923
 */
@Plugin(
        name = "FastJson",
        description = "Reload FastJson cache after class definition/change.",
        testedVersions = {"1.2.83", "2.0.6", "2.0.31"}
)
public class FastJsonPlugin {

    private static final Logger logger = Logger.getLogger(FastJsonPlugin.class);

    public static Set<String> redefine;

    /**
     * fastjson 1+版本
     */
    @OnClassLoadEvent(classNameRegexp = "com.alibaba.fastjson.util.IdentityHashMap")
    public static void patchIdentityHashMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("get", new CtClass[]{classPool.get("java.lang.Object")});
        method.insertBefore("{" +
                "   if ($1 instanceof java.lang.Class && " + FastJsonPlugin.class.getName() + ".redefine != null) {" +
                "       java.lang.Class redefineClass = (java.lang.Class) $1;" +
                "       java.lang.String redefineClassName = redefineClass.getName();" +
                "       if (" + FastJsonPlugin.class.getName() + ".redefine.contains(redefineClassName)) {" +
                "           " + FastJsonPlugin.class.getName() + ".redefine.remove(redefineClassName);" +
                "           put($1, null);" +
                "       }" +
                "   }" +
                "}");
        redefine = new ConcurrentSkipListSet<>();
        logger.info("patch fastjson IdentityHashMap success");
    }

    /**
     * fastjson 1+版本
     */
    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void redefineClass(final Class<?> clazz) {
        if (redefine != null) {
            redefine.add(clazz.getName());
        }
    }

    /**
     * fastjson2、fastjson 2+版本
     */
    @OnClassLoadEvent(classNameRegexp = "com.alibaba.fastjson2.writer.ObjectWriterProvider")
    public static void patchObjectWriterProvider(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass typeCtClass = classPool.get("java.lang.reflect.Type");
        CtClass classCtClass = classPool.get("java.lang.Class");
        CtMethod getObjectWriter = ctClass.getDeclaredMethod("getObjectWriter", new CtClass[]{
                typeCtClass,
                classCtClass,
                CtClass.booleanType
        });
        getObjectWriter.insertBefore("{" +
                "   if ($1 != null) {" +
                "       this.cache.remove($1);" +
                "       this.cacheFieldBased.remove($1);" +
                "   }" +
                "}");

        try {
            // fastjson2
            CtMethod getObjectWriterFromCache = ctClass.getDeclaredMethod("getObjectWriterFromCache", new CtClass[]{
                    typeCtClass,
                    classCtClass,
                    CtClass.booleanType
            });
            getObjectWriterFromCache.setBody("{" +
                    "   return null;" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson 2+ 没有这个方法
        }

        logger.info("patch fastjson2 ObjectWriterProvider success");
    }

    /**
     * fastjson2、fastjson 2+版本
     */
    @OnClassLoadEvent(classNameRegexp = "com.alibaba.fastjson2.util.BeanUtils")
    public static void patchBeanUtils(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass classCtClass = classPool.get("java.lang.Class");
        CtClass consumerCtClass = classPool.get("java.util.function.Consumer");
        CtClass stringCtClass = classPool.get("java.lang.String");
        CtClass objectCtClass = classPool.get("java.lang.Object");
        CtClass methodCtClass = classPool.get("java.lang.reflect.Method");

        CtMethod fields = ctClass.getDeclaredMethod("fields", new CtClass[]{
                classCtClass,
                consumerCtClass
        });
        fields.insertBefore("{" +
                "   if ($1 != null) {" +
                "       fieldCache.remove($1);" +
                "   }" +
                "}");

        try {
            // fastjson 2+
            CtMethod getEnumValueField = ctClass.getDeclaredMethod("getEnumValueField", new CtClass[]{classCtClass});
            getEnumValueField.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       fieldCache.remove($1);" +
                    "       methodCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson2
            CtMethod getEnumValueField = ctClass.getDeclaredMethod("getEnumValueField", new CtClass[]{
                    classCtClass,
                    classPool.get("com.alibaba.fastjson2.modules.ObjectCodecProvider")
            });
            getEnumValueField.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       fieldCache.remove($1);" +
                    "       methodCache.remove($1);" +
                    "   }" +
                    "}");
        }

        CtMethod getDeclaredField = ctClass.getDeclaredMethod("getDeclaredField", new CtClass[]{classCtClass, stringCtClass});
        getDeclaredField.insertBefore("{" +
                "   if ($1 != null) {" +
                "       fieldMapCache.remove($1);" +
                "   }" +
                "}");

        CtMethod declaredFields = ctClass.getDeclaredMethod("declaredFields", new CtClass[]{classCtClass, consumerCtClass});
        declaredFields.insertBefore("{" +
                "   if ($1 != null) {" +
                "       declaredFieldCache.remove($1);" +
                "   }" +
                "}");

        try {
            // fastjson2
            CtMethod setNoneStaticMemberClassParent = ctClass.getDeclaredMethod("setNoneStaticMemberClassParent", new CtClass[]{objectCtClass, objectCtClass});
            setNoneStaticMemberClassParent.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       declaredFieldCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson 2+ 没有这个方法
        }

        CtMethod staticMethod = ctClass.getDeclaredMethod("staticMethod", new CtClass[]{classCtClass, consumerCtClass});
        staticMethod.insertBefore("{" +
                "   if ($1 != null) {" +
                "       methodCache.remove($1);" +
                "   }" +
                "}");

        CtMethod buildMethod = ctClass.getDeclaredMethod("buildMethod", new CtClass[]{classCtClass, stringCtClass});
        buildMethod.insertBefore("{" +
                "   if ($1 != null) {" +
                "       methodCache.remove($1);" +
                "   }" +
                "}");

        CtMethod setters = ctClass.getDeclaredMethod("setters", new CtClass[]{classCtClass, consumerCtClass});
        setters.insertBefore("{" +
                "   if ($1 != null) {" +
                "       methodCache.remove($1);" +
                "   }" +
                "}");

        CtMethod settersBoolean = ctClass.getDeclaredMethod("setters", new CtClass[]{classCtClass, CtClass.booleanType, consumerCtClass});
        settersBoolean.insertBefore("{" +
                "   if ($1 != null) {" +
                "       methodCache.remove($1);" +
                "   }" +
                "}");

        CtMethod annotationMethods = ctClass.getDeclaredMethod("annotationMethods", new CtClass[]{classCtClass, consumerCtClass});
        annotationMethods.insertBefore("{" +
                "   if ($1 != null) {" +
                "       methodCache.remove($1);" +
                "   }" +
                "}");

        try {
            // fastjson 2+
            CtMethod hasStaticCreatorOrBuilder = ctClass.getDeclaredMethod("hasStaticCreatorOrBuilder", new CtClass[]{classCtClass});
            hasStaticCreatorOrBuilder.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       methodCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson2 没有这个方法了
        }

        CtMethod getters = ctClass.getDeclaredMethod("getters", new CtClass[]{classCtClass, consumerCtClass});
        getters.insertBefore("{" +
                "   if ($1 != null) {" +
                "       methodCache.remove($1);" +
                "   }" +
                "}");

        try {
            // fastjson2
            CtMethod getMethodString = ctClass.getDeclaredMethod("getMethod", new CtClass[]{classCtClass, stringCtClass});
            getMethodString.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       methodCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson 2+ 没有这个方法
        }

        try {
            // fastjson2
            CtMethod getMethodClass = ctClass.getDeclaredMethod("getMethod", new CtClass[]{classCtClass, methodCtClass});
            getMethodClass.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       methodCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson 2+ 没有这个方法
        }

        CtMethod getKotlinConstructor = ctClass.getDeclaredMethod("getKotlinConstructor", new CtClass[]{classCtClass, classPool.get("com.alibaba.fastjson2.codec.BeanInfo")});
        getKotlinConstructor.insertBefore("{" +
                "   if ($1 != null) {" +
                "       constructorCache.remove($1);" +
                "   }" +
                "}");

        CtMethod constructor = ctClass.getDeclaredMethod("constructor", new CtClass[]{classCtClass, consumerCtClass});
        constructor.insertBefore("{" +
                "   if ($1 != null) {" +
                "       constructorCache.remove($1);" +
                "   }" +
                "}");

        try {
            // fastjson 2+
            CtMethod getDefaultConstructor = ctClass.getDeclaredMethod("getDefaultConstructor", new CtClass[]{classCtClass});
            getDefaultConstructor.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       constructorCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson2
            CtMethod getDefaultConstructor = ctClass.getDeclaredMethod("getDefaultConstructor", new CtClass[]{classCtClass, CtClass.booleanType});
            getDefaultConstructor.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       constructorCache.remove($1);" +
                    "   }" +
                    "}");
        }

        try {
            // fastjson2
            CtMethod getConstructor = ctClass.getDeclaredMethod("getConstructor", new CtClass[]{classCtClass});
            getConstructor.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       constructorCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson 2+ 没有这个方法
        }

        try {
            // fastjson2
            CtMethod isNoneStaticMemberClass = ctClass.getDeclaredMethod("isNoneStaticMemberClass", new CtClass[]{classCtClass, classCtClass});
            isNoneStaticMemberClass.insertBefore("{" +
                    "   if ($1 != null) {" +
                    "       constructorCache.remove($1);" +
                    "   }" +
                    "}");
        } catch (NotFoundException e) {
            // fastjson 2+ 没有这个方法
        }

        logger.info("patch fastjson2 BeanUtils success");
    }
}
