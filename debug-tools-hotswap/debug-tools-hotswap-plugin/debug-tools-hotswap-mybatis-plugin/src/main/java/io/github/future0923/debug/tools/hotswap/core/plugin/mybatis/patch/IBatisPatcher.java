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
package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.caller.XPathParserCaller;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload.MyBatisSpringResourceManager;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;

/**
 * @author future0923
 */
public class IBatisPatcher {

    private static final Logger logger = Logger.getLogger(IBatisPatcher.class);

    /**
     * 文件名字段
     */
    public static final String SRC_FILE_NAME_FIELD = "$$ha$srcFileName";

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.session.Configuration")
    public static void patchConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod addMappedStatementMethod = ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")});
        addMappedStatementMethod.setBody("{" +
                "   if(mappedStatements.containsKey($1.getId())){" +
                "       mappedStatements.remove($1.getId());" +
                "   }" +
                "   mappedStatements.put($1.getId(),$1);" +
                "}");
        CtMethod addParameterMapMethod = ctClass.getDeclaredMethod("addParameterMap", new CtClass[]{classPool.get("org.apache.ibatis.mapping.ParameterMap")});
        addParameterMapMethod.setBody("{" +
                "   if(parameterMaps.containsKey($1.getId())){" +
                "       parameterMaps.remove($1.getId());" +
                "   }" +
                "   parameterMaps.put($1.getId(),$1);" +
                "}");
        CtMethod addResultMapMethod = ctClass.getDeclaredMethod("addResultMap", new CtClass[]{classPool.get("org.apache.ibatis.mapping.ResultMap")});
        addResultMapMethod.setBody("{" +
                "   if(resultMaps.containsKey($1.getId())){" +
                "       resultMaps.remove($1.getId());" +
                "   }" +
                "   resultMaps.put($1.getId(),$1);" +
                "   checkLocallyForDiscriminatedNestedResultMaps($1);" +
                "   checkGloballyForDiscriminatedNestedResultMaps($1);" +
                "}");
        CtMethod addKeyGeneratorMethod = ctClass.getDeclaredMethod("addKeyGenerator", new CtClass[]{classPool.get("java.lang.String"), classPool.get("org.apache.ibatis.executor.keygen.KeyGenerator")});
        addKeyGeneratorMethod.setBody("{" +
                "   if(keyGenerators.containsKey($1)){" +
                "       keyGenerators.remove($1);" +
                "   }" +
                "   keyGenerators.put($1,$2);" +
                "}");
        CtMethod addCacheMethod = ctClass.getDeclaredMethod("addCache", new CtClass[]{classPool.get("org.apache.ibatis.cache.Cache")});
        addCacheMethod.setBody("{" +
                "   if(caches.containsKey($1.getId())){" +
                "       caches.remove($1.getId());" +
                "   }" +
                "   caches.put($1.getId(),$1);" +
                "}");
        for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
            constructor.insertAfter("{" +
                        MyBatisSpringResourceManager.class.getName() + ".registerConfiguration(this);" +
                    "}");
        }
    }

    /**
     * 实际上是 org.apache.ibatis.session.Configuration$StrictMap，将 $ 换成 . 也可以识别
     * 写 $ 时 org.apache.ibatis.session.Configuration 主类就获取不到了，不知道为啥
     */
    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.session.Configuration.StrictMap")
    public static void patchConfigurationStrictMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put",new CtClass[]{classPool.get("java.lang.String"), classPool.get("java.lang.Object")});
        method.insertBefore("{" +
                "   remove($1);" +
                "}");
    }

    /**
     * 增加解析的xml文件名字字段
     * XPathParser 是一个工具类，用于解析 XML 配置文件。它的核心功能是处理 XML 数据，通过 XPath 表达式读取和操作节点内容
     */
    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.parsing.XPathParser")
    public static void patchXPathParser(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        logger.debug("org.apache.ibatis.parsing.XPathParser patched.");
        CtClass stringClass = classPool.get("java.lang.String");
        CtField sourceFileField = new CtField(stringClass, SRC_FILE_NAME_FIELD, ctClass);
        ctClass.addField(sourceFileField);
        // createDocument 方法中，获取文件名，并赋值给 SRC_FILE_NAME_FIELD 字段
        CtMethod method = ctClass.getDeclaredMethod("createDocument");
        method.insertBefore("{" +
                "this." + SRC_FILE_NAME_FIELD + " = " + IOUtils.class.getName() + ".extractFileNameFromInputSource($1);" +
                "}"
        );
    }


    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.xml.XMLMapperBuilder")
    public static void patchXMLMapperBuilder(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        logger.debug("org.apache.ibatis.builder.xml.XMLMapperBuilder patched.");
        CtClass[] constructorParams = new CtClass[]{
                classPool.get("org.apache.ibatis.parsing.XPathParser"),
                classPool.get("org.apache.ibatis.session.Configuration"),
                classPool.get("java.lang.String"),
                classPool.get("java.util.Map")
        };
        CtConstructor constructor = ctClass.getDeclaredConstructor(constructorParams);
        String src = "{" +
                PluginManagerInvoker.buildInitializePlugin(MyBatisPlugin.class) +
                PluginManagerInvoker.buildCallPluginMethod(MyBatisPlugin.class, "init", "org.apache.ibatis.builder.xml.XMLMapperBuilder.class.getClassLoader()", ClassLoader.class.getName()) +
                PluginManagerInvoker.buildCallPluginMethod(
                        MyBatisPlugin.class,
                        "registerConfigurationFile",
                        XPathParserCaller.class.getName() + ".getSrcFileName(this.parser)",
                        "java.lang.String",
                        "this",
                        "java.lang.Object") +
                "}";
        constructor.insertAfter(src);
    }

}
