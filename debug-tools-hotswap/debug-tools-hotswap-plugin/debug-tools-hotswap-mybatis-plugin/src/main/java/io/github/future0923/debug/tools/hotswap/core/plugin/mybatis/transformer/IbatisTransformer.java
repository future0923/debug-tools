package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtField;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.ConfigurationProxy;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.InstancesHolder;
import org.apache.ibatis.javassist.bytecode.AccessFlag;

/**
 * @author future0923
 */
public class IbatisTransformer {

    private static final Logger LOGGER = Logger.getLogger(IbatisTransformer.class);

    public static final String SRC_FILE_NAME_FIELD = "$$ha$srcFileName";

    public static final String REFRESH_DOCUMENT_METHOD = "$$ha$refreshDocument";

    public static final String REFRESH_METHOD = "$$ha$refresh";

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.parsing.XPathParser")
    public static void patchXPathParser(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass stringClass = classPool.get("java.lang.String");
        CtField sourceFileField = new CtField(stringClass, SRC_FILE_NAME_FIELD, ctClass);
        ctClass.addField(sourceFileField);

        CtMethod method = ctClass.getDeclaredMethod("createDocument");
        method.insertBefore("{" +
                "this." + SRC_FILE_NAME_FIELD + " = " + IOUtils.class.getName() + ".extractFileNameFromInputSource($1);" +
                "}"
        );
        CtMethod newMethod = CtNewMethod.make(
                "public boolean " + REFRESH_DOCUMENT_METHOD + "() {" +
                        "if(this." + SRC_FILE_NAME_FIELD + "!=null) {" +
                        "this.document=createDocument(new org.xml.sax.InputSource(new java.io.FileReader(this." + SRC_FILE_NAME_FIELD + ")));" +
                        "return true;" +
                        "}" +
                        "return false;" +
                        "}", ctClass);
        ctClass.addMethod(newMethod);
        LOGGER.debug("org.apache.ibatis.parsing.XPathParser patched.");
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.BaseBuilder")
    public static void patchBaseBuilder(CtClass ctClass) throws NotFoundException, CannotCompileException {
        LOGGER.debug("org.apache.ibatis.builder.BaseBuilder patched.");
        CtField configField = ctClass.getField("configuration");
        configField.setModifiers(configField.getModifiers() & ~AccessFlag.FINAL);
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.xml.XMLConfigBuilder")
    public static void patchXMLConfigBuilder(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {

        StringBuilder src = new StringBuilder("{");
        src.append(PluginManagerInvoker.buildInitializePlugin(MyBatisPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(MyBatisPlugin.class, "registerConfigurationFile",
                XPathParserCaller.class.getName() + ".getSrcFileName(this.parser)", "java.lang.String", "this", "java.lang.Object"));
        src.append("this.configuration = ").append(ConfigurationProxy.class.getName()).append(".getWrapper(this).proxy(this.configuration);");
        src.append("}");

        CtClass[] constructorParams = new CtClass[] {
                classPool.get("org.apache.ibatis.parsing.XPathParser"),
                classPool.get("java.lang.String"),
                classPool.get("java.util.Properties")
        };

        ctClass.getDeclaredConstructor(constructorParams).insertAfter(src.toString());
        CtMethod newMethod = CtNewMethod.make(
                "public void " + REFRESH_METHOD + "() {" +
                        "if(" + XPathParserCaller.class.getName() + ".refreshDocument(this.parser)) {" +
                        "this.parsed=false;" +
                        "parse();" +
                        "}" +
                        "}", ctClass);
        ctClass.addMethod(newMethod);
        LOGGER.debug("org.apache.ibatis.builder.xml.XMLConfigBuilder patched.");
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.builder.xml.XMLMapperBuilder")
    public static void patchXMLMapperBuilder(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        StringBuilder src = new StringBuilder("{");
        src.append(PluginManagerInvoker.buildInitializePlugin(MyBatisPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(MyBatisPlugin.class, "registerConfigurationFile",
                XPathParserCaller.class.getName() + ".getSrcFileName(this.parser)", "java.lang.String", "this", "java.lang.Object"));
        src.append("}");

        CtClass[] constructorParams = new CtClass[] {
                classPool.get("org.apache.ibatis.parsing.XPathParser"),
                classPool.get("org.apache.ibatis.session.Configuration"),
                classPool.get("java.lang.String"),
                classPool.get("java.util.Map")
        };

        CtConstructor constructor = ctClass.getDeclaredConstructor(constructorParams);
        constructor.insertAfter(src.toString());
        LOGGER.debug("org.apache.ibatis.builder.xml.XMLMapperBuilder patched.");
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.session.defaults.DefaultSqlSessionFactory")
    public static void patchDefaultSqlSessionFactory(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        ctClass.addField(CtField.make("public static java.util.ArrayList  _staticConfiguration = new java.util.ArrayList();", ctClass));
        CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[] { classPool.get("org.apache.ibatis.session.Configuration")});
        constructor.insertAfter("{_staticConfiguration.add($1);}");
        LOGGER.debug("org.apache.ibatis.session.defaults.DefaultSqlSessionFactory patched.");
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.session.Configuration")
    public static void transformConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        try {
            //CtMethod addMappedStatementMethod = ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")});
            //addMappedStatementMethod.setBody("{if(mappedStatements.containsKey($1.getId())){mappedStatements.remove($1.getId());}mappedStatements.put($1.getId(),$1);}");
            //CtMethod addParameterMapMethod = ctClass.getDeclaredMethod("addParameterMap", new CtClass[]{classPool.get("org.apache.ibatis.mapping.ParameterMap")});
            //addParameterMapMethod.setBody("{if(parameterMaps.containsKey($1.getId())){parameterMaps.remove($1.getId());}parameterMaps.put($1.getId(),$1);}");
            //CtMethod addResultMapMethod = ctClass.getDeclaredMethod("addResultMap", new CtClass[]{classPool.get("org.apache.ibatis.mapping.ResultMap")});
            //addResultMapMethod.setBody("{if(resultMaps.containsKey($1.getId())){resultMaps.remove($1.getId());}resultMaps.put($1.getId(),$1);checkLocallyForDiscriminatedNestedResultMaps($1);checkGloballyForDiscriminatedNestedResultMaps($1);}");
            //CtMethod addKeyGeneratorMethod = ctClass.getDeclaredMethod("addKeyGenerator", new CtClass[]{classPool.get("java.lang.String"), classPool.get("org.apache.ibatis.executor.keygen.KeyGenerator")});
            //addKeyGeneratorMethod.setBody("{if(keyGenerators.containsKey($1)){keyGenerators.remove($1);}keyGenerators.put($1,$2);}");
            //CtMethod addCacheMethod = ctClass.getDeclaredMethod("addCache", new CtClass[]{classPool.get("org.apache.ibatis.cache.Cache")});
            //addCacheMethod.setBody("{if(caches.containsKey($1.getId())){caches.remove($1.getId());}caches.put($1.getId(),$1);}");
            InstancesHolder.insertObjectCacheInConstructorWithBaseClassKey(ctClass);
        } catch (Throwable var9) {
            LOGGER.warning("mybatis class enhance error:" + var9.getMessage(), new Object[0]);
        }
    }

    @OnClassLoadEvent(
            classNameRegexp = "org.apache.ibatis.session.Configuration\\$StrictMap"
    )
    public static void patchStrictMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put", new CtClass[]{classPool.get(String.class.getName()), classPool.get(Object.class.getName())});
        method.insertBefore("if(containsKey($1)){remove($1);}");
    }
}
