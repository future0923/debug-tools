package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Command;
import io.github.future0923.debug.tools.hotswap.core.command.ReflectionCommand;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.ConfigurationProxy;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.caller.XPathParserCaller;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisConfigurationCommand;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import org.apache.ibatis.javassist.bytecode.AccessFlag;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author future0923
 */
public class MybatisXmlRegister {

    private static final Logger logger = Logger.getLogger(MybatisXmlRegister.class);

    /**
     * 文件名字段
     */
    public static final String SRC_FILE_NAME_FIELD = "$$ha$srcFileName";

    /**
     * 刷新Document方法
     */
    public static final String REFRESH_DOCUMENT_METHOD = "$$ha$refreshDocument";

    /**
     * 刷新方法
     */
    public static final String REFRESH_METHOD = "$$ha$refresh";

    private static final Map<String, Object> configurationMap = new HashMap<>();

    private static final Command reloadConfigurationCommand = new ReflectionCommand(null, MyBatisConfigurationCommand.class.getName(), "reloadConfiguration");

    @Init
    static Scheduler scheduler;

    /**
     * 增加刷新 Document 方法，用于刷新 XPathParser 类中的 document 属性
     * XPathParser 是一个工具类，用于解析 XML 配置文件。它的核心功能是处理 XML 数据，通过 XPath 表达式读取和操作节点内容
     */
    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.parsing.XPathParser")
    public static void patchXPathParser(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass stringClass = classPool.get("java.lang.String");
        CtField sourceFileField = new CtField(stringClass, SRC_FILE_NAME_FIELD, ctClass);
        ctClass.addField(sourceFileField);
        // createDocument 方法中，获取文件名，并赋值给 SRC_FILE_NAME_FIELD 字段
        CtMethod method = ctClass.getDeclaredMethod("createDocument");
        method.insertBefore("{" +
                "this." + SRC_FILE_NAME_FIELD + " = " + IOUtils.class.getName() + ".extractFileNameFromInputSource($1);" +
                "}"
        );
        // 增加刷新Document方法，就是重新创建 XPathParser 类中的 document 属性
        CtMethod newMethod = CtNewMethod.make(
                "public boolean " + REFRESH_DOCUMENT_METHOD + "() {" +
                        "if(this." + SRC_FILE_NAME_FIELD + " != null) {" +
                        "   this.document = createDocument(new org.xml.sax.InputSource(new java.io.FileReader(this." + SRC_FILE_NAME_FIELD + ")));" +
                        "   return true;" +
                        "}" +
                    "return false;" +
                "}", ctClass);
        ctClass.addMethod(newMethod);
        logger.debug("org.apache.ibatis.parsing.XPathParser patched.");
    }

    /**
     * 移除 BaseBuilder 中 configuration 字段的 final 修饰符
     * BaseBuilder 是配置构建阶段的核心部分。它的主要作用是提供一些通用的工具和方法，用于解析 MyBatis 配置文件
     */
    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.BaseBuilder")
    public static void patchBaseBuilder(CtClass ctClass) throws NotFoundException, CannotCompileException {
        logger.debug("org.apache.ibatis.builder.BaseBuilder patched.");
        CtField configField = ctClass.getField("configuration");
        configField.setModifiers(configField.getModifiers() & ~AccessFlag.FINAL);
    }

    /**
     * XMLConfigBuilder 是 MyBatis 的核心类，作用是解析 XML 配置文件，并生成全局配置对象 Configuration
     */
    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.xml.XMLConfigBuilder")
    public static void patchXMLConfigBuilder(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass[] constructorParams = new CtClass[]{
                classPool.get("org.apache.ibatis.parsing.XPathParser"),
                classPool.get("java.lang.String"),
                classPool.get("java.util.Properties")
        };
        CtConstructor ctConstructor = ctClass.getDeclaredConstructor(constructorParams);
        String src = "{" +
                MybatisXmlRegister.class.getName() + ".registerConfigurationFile (" + XPathParserCaller.class.getName() + ".getSrcFileName(this.parser), this);" +
                "this.configuration = " + ConfigurationProxy.class.getName() + ".getWrapper(this).proxy(this.configuration);" +
        "}";
        ctConstructor.insertAfter(src);
        CtMethod newMethod = CtNewMethod.make(
                "public void " + REFRESH_METHOD + "() {" +
                        "if (" + XPathParserCaller.class.getName() + ".refreshDocument(this.parser)) {" +
                        "   this.parsed = false;" +
                        "   parse();" +
                        "}" +
                    "}",
                ctClass);
        ctClass.addMethod(newMethod);
        logger.debug("org.apache.ibatis.builder.xml.XMLConfigBuilder patched.");
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.xml.XMLMapperBuilder")
    public static void patchXMLMapperBuilder(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass[] constructorParams = new CtClass[]{
                classPool.get("org.apache.ibatis.parsing.XPathParser"),
                classPool.get("org.apache.ibatis.session.Configuration"),
                classPool.get("java.lang.String"),
                classPool.get("java.util.Map")
        };
        CtConstructor constructor = ctClass.getDeclaredConstructor(constructorParams);
        String src = "{" +
                MybatisXmlRegister.class.getName() + ".registerConfigurationFile (" + XPathParserCaller.class.getName() + ".getSrcFileName(this.parser), this);" +
                "}";
        constructor.insertAfter(src);
        logger.debug("org.apache.ibatis.builder.xml.XMLMapperBuilder patched.");
    }

    public static void registerConfigurationFile(String configFile, Object configObject) {
        if (configFile != null && !configurationMap.containsKey(configFile)) {
            logger.debug("MyBatisPlugin - configuration file registered : {}", configFile);
            configurationMap.put(configFile, configObject);
        }
    }

    @OnResourceFileEvent(path="/", filter = ".*.xml", events = {FileEvent.MODIFY})
    public void registerResourceListeners(URL url) {
        if (configurationMap.containsKey(url.getPath())) {
            scheduler.scheduleCommand(reloadConfigurationCommand, 500);
        }
    }
}
