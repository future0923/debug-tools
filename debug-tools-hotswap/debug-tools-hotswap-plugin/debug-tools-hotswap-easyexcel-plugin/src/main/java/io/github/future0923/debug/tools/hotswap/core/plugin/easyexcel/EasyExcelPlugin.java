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
import io.github.future0923.debug.tools.hotswap.core.util.JavassistUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import java.io.IOException;

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
        String beforeHandler =
                "if (this.headClazz != null) {" +
                "    this.headMap.clear();" +
                "    this.initColumnProperties(this.holder);" +
                "    this.initHeadRowNumber();" +
                "    com.alibaba.excel.annotation.write.style.ColumnWidth parentColumnWidth = this.headClazz.getAnnotation(com.alibaba.excel.annotation.write.style.ColumnWidth.class);" +
                "    com.alibaba.excel.annotation.write.style.HeadStyle parentHeadStyle = this.headClazz.getAnnotation(com.alibaba.excel.annotation.write.style.HeadStyle.class);" +
                "    com.alibaba.excel.annotation.write.style.HeadFontStyle parentHeadFontStyle = this.headClazz.getAnnotation(com.alibaba.excel.annotation.write.style.HeadFontStyle.class);" +
                "    java.util.Set entrySet = this.headMap.entrySet();" +
                "    java.util.Iterator iterator = entrySet.iterator();" +
                "    while (iterator.hasNext()) {" +
                "        Object obj = iterator.next();" +
                "        java.util.Map.Entry entry = (java.util.Map.Entry) obj;" +
                "        com.alibaba.excel.metadata.Head headData = (com.alibaba.excel.metadata.Head) entry.getValue();" +
                "        if (headData == null) {" +
                "            continue;" +
                "        }" +
                "        java.lang.reflect.Field field = headData.getField();" +
                "        com.alibaba.excel.annotation.write.style.ColumnWidth columnWidth = null;" +
                "        if (field != null) {" +
                "            columnWidth = field.getAnnotation(com.alibaba.excel.annotation.write.style.ColumnWidth.class);" +
                "        }" +
                "        if (columnWidth == null) {" +
                "            columnWidth = parentColumnWidth;" +
                "        }" +
                "        com.alibaba.excel.annotation.write.style.HeadStyle headStyle = null;" +
                "        if (field != null) {" +
                "            headStyle = field.getAnnotation(com.alibaba.excel.annotation.write.style.HeadStyle.class);" +
                "        }" +
                "        if (headStyle == null) {" +
                "            headStyle = parentHeadStyle;" +
                "        }" +
                "        com.alibaba.excel.annotation.write.style.HeadFontStyle headFontStyle = null;" +
                "        if (field != null) {" +
                "            headFontStyle = field.getAnnotation(com.alibaba.excel.annotation.write.style.HeadFontStyle.class);" +
                "        }" +
                "        if (headFontStyle == null) {" +
                "            headFontStyle = parentHeadFontStyle;" +
                "        }" +
                "        com.alibaba.excel.annotation.write.style.ContentLoopMerge contentLoopMerge = null;" +
                "        if (field != null) {" +
                "            contentLoopMerge = field.getAnnotation(com.alibaba.excel.annotation.write.style.ContentLoopMerge.class);" +
                "        }" +
                "        headData.setColumnWidthProperty(com.alibaba.excel.metadata.property.ColumnWidthProperty.build(columnWidth));" +
                "        headData.setHeadStyleProperty(com.alibaba.excel.metadata.property.StyleProperty.build(headStyle));" +
                "        headData.setHeadFontProperty(com.alibaba.excel.metadata.property.FontProperty.build(headFontStyle));" +
                "        headData.setLoopMergeProperty(com.alibaba.excel.metadata.property.LoopMergeProperty.build(contentLoopMerge));" +
                "    }" +
                "}";
        getHeadMap.insertBefore("{" + beforeHandler + "}");
        logger.info("patch easy excel ExcelHeadProperty success");
    }

    @OnClassLoadEvent(classNameRegexp = "com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty")
    public static void patchExcelReadHeadProperty(CtClass ctClass, ClassPool classPool, ClassLoader classLoader) throws CannotCompileException, NotFoundException, IOException {
        CtClass mapClass = classPool.get("java.util.Map");
        CtField tmpHeadMap = new CtField(mapClass, "tmpHeadMap", ctClass);
        tmpHeadMap.setModifiers(Modifier.PRIVATE);
        tmpHeadMap.setGenericSignature("Ljava/util/Map<Ljava/lang/Integer;Lcom/alibaba/excel/metadata/Head;>;");
        ctClass.addField(tmpHeadMap);
        CtMethod getTmpHeadMapMethod = CtNewMethod.getter("getTmpHeadMap", tmpHeadMap);
        getTmpHeadMapMethod.setGenericSignature("()Ljava/util/Map<Ljava/lang/Integer;Lcom/alibaba/excel/metadata/Head;>;");
        ctClass.addMethod(getTmpHeadMapMethod);
        CtMethod setTmpHeadMapMethod = CtNewMethod.setter("setTmpHeadMap", tmpHeadMap);
        setTmpHeadMapMethod.setGenericSignature("(Ljava/util/Map<Ljava/lang/Integer;Lcom/alibaba/excel/metadata/Head;>;)V");
        ctClass.addMethod(setTmpHeadMapMethod);
        JavassistUtil.insertClassPath(classLoader, "com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty", ctClass);
    }

    @OnClassLoadEvent(classNameRegexp = "com.alibaba.excel.read.listener.ModelBuildEventListener")
    public static void patchModelBuildEventListener(CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtMethod buildUserModelMethod = ctClass.getDeclaredMethod("buildUserModel");
        buildUserModelMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty")
                        && m.getMethodName().equals("getHeadMap")) {
                    m.replace("{ $_ = $0.getTmpHeadMap(); }");
                }
            }
        });
    }

    @OnClassLoadEvent(classNameRegexp = "com.alibaba.excel.read.processor.DefaultAnalysisEventProcessor")
    public static void patchDefaultAnalysisEventProcessor(CtClass ctClass) throws CannotCompileException, NotFoundException {
        CtMethod buildHeadMethod = ctClass.getDeclaredMethod("buildHead");
        buildHeadMethod.instrument(new ExprEditor() {
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                if (m.getClassName().equals("com.alibaba.excel.read.metadata.property.ExcelReadHeadProperty")
                        && m.getMethodName().equals("setHeadMap")) {
                    m.replace("{ $0.setTmpHeadMap($1); }");
                }
            }
        });
    }

}
