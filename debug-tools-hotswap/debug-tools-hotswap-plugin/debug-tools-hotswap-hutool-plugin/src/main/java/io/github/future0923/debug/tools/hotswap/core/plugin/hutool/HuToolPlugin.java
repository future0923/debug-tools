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
package io.github.future0923.debug.tools.hotswap.core.plugin.hutool;

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
        name = "HuTool",
        description = "Reload HuTool cache after class definition/change.",
        testedVersions = {"All between 5.3.2"},
        expectedVersions = {"5.3.2"}
)
public class HuToolPlugin {

    private static final Logger logger = Logger.getLogger(HuToolPlugin.class);

    @OnClassLoadEvent(classNameRegexp = "cn.hutool.core.util.ReflectUtil")
    public static void patchReflectUtil(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getFields = ctClass.getDeclaredMethod("getFields", new CtClass[]{classPool.get("java.lang.Class")});
        getFields.setBody("{" +
                "   cn.hutool.core.lang.Assert.notNull($1);" +
                "   return cn.hutool.core.util.ReflectUtil.getFieldsDirectly($1, true);" +
                "}");

        CtMethod getMethods = ctClass.getDeclaredMethod("getMethods", new CtClass[]{classPool.get("java.lang.Class")});
        getMethods.setBody("{" +
                "   cn.hutool.core.lang.Assert.notNull($1);" +
                "   return cn.hutool.core.util.ReflectUtil.getMethodsDirectly($1, true, true);" +
                "}");

        CtMethod getConstructors = ctClass.getDeclaredMethod("getConstructors", new CtClass[]{classPool.get("java.lang.Class")});
        getConstructors.setBody("{" +
                "   cn.hutool.core.lang.Assert.notNull($1);" +
                "   return cn.hutool.core.util.ReflectUtil.getConstructorsDirectly($1);" +
                "}");
        logger.info("patch hutool ReflectUtil success");
    }

    @OnClassLoadEvent(classNameRegexp = "cn.hutool.core.bean.BeanDescCache")
    public static void patchBeanDescCache(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getFields = ctClass.getDeclaredMethod("getBeanDesc", new CtClass[]{classPool.get("java.lang.Class"), classPool.get("cn.hutool.core.lang.func.Func0")});
        getFields.setBody("{" +
                "return (cn.hutool.core.bean.BeanDesc) $2.callWithRuntimeException();" +
                "}");
        logger.info("patch hutool BeanDescCache success");
    }

    @OnClassLoadEvent(classNameRegexp = "io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil")
    public static void patchDebugToolsReflectUtil(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getFields = ctClass.getDeclaredMethod("getFields", new CtClass[]{classPool.get("java.lang.Class")});
        getFields.setBody("{" +
                "   io.github.future0923.debug.tools.base.hutool.core.lang.Assert.notNull($1);" +
                "   return io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil.getFieldsDirectly($1, true);" +
                "}");

        CtMethod getMethods = ctClass.getDeclaredMethod("getMethods", new CtClass[]{classPool.get("java.lang.Class")});
        getMethods.setBody("{" +
                "   io.github.future0923.debug.tools.base.hutool.core.lang.Assert.notNull($1);" +
                "   return io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil.getMethodsDirectly($1, true, true);" +
                "}");

        CtMethod getConstructors = ctClass.getDeclaredMethod("getConstructors", new CtClass[]{classPool.get("java.lang.Class")});
        getConstructors.setBody("{" +
                "   io.github.future0923.debug.tools.base.hutool.core.lang.Assert.notNull($1);" +
                "   return io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil.getConstructorsDirectly($1);" +
                "}");
    }

    @OnClassLoadEvent(classNameRegexp = "io.github.future0923.debug.tools.base.hutool.core.bean.BeanDescCache")
    public static void patchDebugToolsBeanDescCache(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getFields = ctClass.getDeclaredMethod("getBeanDesc", new CtClass[]{classPool.get("java.lang.Class"), classPool.get("io.github.future0923.debug.tools.base.hutool.core.lang.func.Func0")});
        getFields.setBody("{" +
                "return (io.github.future0923.debug.tools.base.hutool.core.bean.BeanDesc) $2.callWithRuntimeException();" +
                "}");
    }

}
