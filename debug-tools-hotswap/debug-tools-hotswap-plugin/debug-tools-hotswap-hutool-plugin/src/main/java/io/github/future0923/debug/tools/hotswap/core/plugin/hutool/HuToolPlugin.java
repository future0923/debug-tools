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
