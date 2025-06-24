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
package io.github.future0923.debug.tools.hotswap.core.plugin.easyexcel;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * @author future0923
 */
@Plugin(
        name = "EasyExcel",
        description = "Reload EasyExcel cache after class definition/change.",
        testedVersions = {"All between 4.0.3"}
)
public class EasyExcelPlugin {

    private static final Logger logger = Logger.getLogger(EasyExcelPlugin.class);

    @OnClassLoadEvent(classNameRegexp = "com.alibaba.excel.util.ClassUtils")
    public static void patchClassUtils(CtClass ctClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
        CtClass classCtClass = classPool.get("java.lang.Class");
        CtClass stringCtClass = classPool.get("java.lang.String");
        try {
            // 4+
            CtClass holderCtClass = classPool.get("com.alibaba.excel.metadata.ConfigurationHolder");
            CtMethod declaredFields = ctClass.getDeclaredMethod("declaredFields", new CtClass[]{classCtClass, holderCtClass});
            declaredFields.insertBefore("{" +
                    "   com.alibaba.excel.util.ClassUtils.FIELD_CACHE.clear();" +
                    "   com.alibaba.excel.util.ClassUtils.FIELD_THREAD_LOCAL.remove();" +
                    "}");
            CtMethod declaredFieldContentMap = ctClass.getDeclaredMethod("declaredFieldContentMap", new CtClass[]{classCtClass, holderCtClass});
            declaredFieldContentMap.insertBefore("{" +
                    "   com.alibaba.excel.util.ClassUtils.CLASS_CONTENT_CACHE.clear();" +
                    "   com.alibaba.excel.util.ClassUtils.CLASS_CONTENT_THREAD_LOCAL.remove();" +
                    "}");
            CtMethod getExcelContentProperty = ctClass.getDeclaredMethod("getExcelContentProperty", new CtClass[]{classCtClass, classCtClass, stringCtClass, holderCtClass});
            getExcelContentProperty.insertBefore("{" +
                    "   com.alibaba.excel.util.ClassUtils.CONTENT_CACHE.clear();" +
                    "   com.alibaba.excel.util.ClassUtils.CONTENT_THREAD_LOCAL.remove();" +
                    "}");
        } catch (NotFoundException e) {
            // 3+
            CtMethod declaredFields = ctClass.getDeclaredMethod("declaredFields", new CtClass[]{classCtClass});
            declaredFields.insertBefore("{" +
                    "   com.alibaba.excel.util.ClassUtils.FIELD_CACHE.clear();" +
                    "}");
            CtMethod declaredFieldContentMap = ctClass.getDeclaredMethod("declaredFieldContentMap", new CtClass[]{classCtClass});
            declaredFieldContentMap.insertBefore("{" +
                    "   com.alibaba.excel.util.ClassUtils.CLASS_CONTENT_CACHE.clear();" +
                    "}");
            CtMethod getExcelContentProperty = ctClass.getDeclaredMethod("getExcelContentProperty", new CtClass[]{classCtClass, classCtClass, stringCtClass});
            getExcelContentProperty.insertBefore("{" +
                    "   com.alibaba.excel.util.ClassUtils.CONTENT_CACHE.clear();" +
                    "}");
        }
        logger.info("patch easy excel ClassUtils success");
    }

    @OnClassLoadEvent(classNameRegexp = "com.alibaba.excel.metadata.property.ExcelHeadProperty")
    public static void patchExcelHeadProperty(CtClass ctClass, ClassPool classPool) throws CannotCompileException, NotFoundException {
        try {
            // 4+
            CtClass holder = classPool.get("com.alibaba.excel.metadata.ConfigurationHolder");
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{holder, classPool.get("java.lang.Class"), classPool.get("java.util.List")});
            CtField holderField = new CtField(holder, "holder", ctClass);
            ctClass.addField(holderField);
            constructor.insertBefore("{" +
                    "   this.holder = $1;" +
                    "}");
        } catch (NotFoundException e) {
            CtClass holder = classPool.get("com.alibaba.excel.metadata.Holder");
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{holder, classPool.get("java.lang.Class"), classPool.get("java.util.List")});
            CtField holderField = new CtField(holder, "holder", ctClass);
            ctClass.addField(holderField);
            constructor.insertBefore("{" +
                    "   this.holder = $1;" +
                    "}");
        }
        CtMethod getHeadMap = ctClass.getDeclaredMethod("getHeadMap");
        getHeadMap.insertBefore("{" +
                "   if (this.headClazz != null) {" +
                "       this.headMap.clear();" +
                "       this.initColumnProperties(this.holder);" +
                "       this.initHeadRowNumber();" +
                "   }" +
                "}");
        logger.info("patch easy excel ExcelHeadProperty success");
    }

}
