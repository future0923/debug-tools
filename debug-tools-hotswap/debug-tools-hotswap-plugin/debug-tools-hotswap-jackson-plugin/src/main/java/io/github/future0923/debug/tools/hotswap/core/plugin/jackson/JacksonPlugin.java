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
package io.github.future0923.debug.tools.hotswap.core.plugin.jackson;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * @author future0923
 */
@Plugin(
        name = "Jackson",
        description = "Reload Jackson cache after class definition/change.",
        testedVersions = {"All between 2.13.4"}
)
public class JacksonPlugin {

    private static final Logger logger = Logger.getLogger(JacksonPlugin.class);

    @OnClassLoadEvent(classNameRegexp = "com.fasterxml.jackson.databind.ser.impl.ReadOnlyClassToSerializerMap")
    public static void patchReadOnlyClassToSerializerMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        extracted(ctClass, classPool);
        logger.info("patch jackson ReadOnlyClassToSerializerMap success");
    }

    @OnClassLoadEvent(classNameRegexp = "com.fasterxml.jackson.databind.ser.SerializerCache")
    public static void patchSerializerCache(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        extracted(ctClass, classPool);
        logger.info("patch jackson SerializerCache success");
    }

    private static void extracted(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass javaType = classPool.get("com.fasterxml.jackson.databind.JavaType");
        CtClass classCtClass = classPool.get("java.lang.Class");

        CtMethod typedValueSerializerByJavaType = ctClass.getDeclaredMethod("typedValueSerializer", new CtClass[]{javaType});
        typedValueSerializerByJavaType.setBody("{" +
                "   return null;" +
                "}");

        CtMethod typedValueSerializerByClass = ctClass.getDeclaredMethod("typedValueSerializer", new CtClass[]{classCtClass});
        typedValueSerializerByClass.setBody("{" +
                "   return null;" +
                "}");

        CtMethod untypedValueSerializerByJavaType = ctClass.getDeclaredMethod("untypedValueSerializer", new CtClass[]{javaType});
        untypedValueSerializerByJavaType.setBody("{" +
                "   return null;" +
                "}");

        CtMethod untypedValueSerializerByClass = ctClass.getDeclaredMethod("untypedValueSerializer", new CtClass[]{classCtClass});
        untypedValueSerializerByClass.setBody("{" +
                "   return null;" +
                "}");
    }

    @OnClassLoadEvent(classNameRegexp = "com.fasterxml.jackson.databind.ObjectMapper")
    public static void patchObjectMapper(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod _findRootDeserializer = ctClass.getDeclaredMethod("_findRootDeserializer",
                new CtClass[]{
                        classPool.get("com.fasterxml.jackson.databind.DeserializationContext"),
                        classPool.get("com.fasterxml.jackson.databind.JavaType")
                }
        );
        _findRootDeserializer.insertBefore("{" +
                "   _rootDeserializers.remove($2);" +
                "}");
        logger.info("patch jackson ObjectMapper success");
    }

    @OnClassLoadEvent(classNameRegexp = "com.fasterxml.jackson.databind.deser.DeserializerCache")
    public static void jsonDeserializerCacheClear(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass javaType = classPool.get("com.fasterxml.jackson.databind.JavaType");

        CtMethod _findCachedDeserializer = ctClass.getDeclaredMethod("_findCachedDeserializer", new CtClass[]{javaType});
        _findCachedDeserializer.setBody("{" +
                "   if (type == null) {" +
                "       throw new java.langIllegalArgumentException(\"Null JavaType passed\");" +
                "   }" +
                "   return null;" +
                "}");

        CtMethod _createAndCacheValueDeserializer = ctClass.getDeclaredMethod("_createAndCacheValueDeserializer",
                new CtClass[]{
                        classPool.get("com.fasterxml.jackson.databind.DeserializationContext"),
                        classPool.get("com.fasterxml.jackson.databind.deser.DeserializerFactory"),
                        javaType
                }
        );
        _createAndCacheValueDeserializer.insertBefore("{" +
                "   _incompleteDeserializers.remove($3);" +
                "}");

        logger.info("patch jackson DeserializerCache success");
    }
}
