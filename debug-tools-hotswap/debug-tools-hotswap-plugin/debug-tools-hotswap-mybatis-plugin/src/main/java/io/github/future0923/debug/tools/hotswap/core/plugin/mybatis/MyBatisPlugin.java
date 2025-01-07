package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MyBatisEntityRegister;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MyBatisMapperRegister;

/**
 * Mybatis热重载插件
 */
@Plugin(name = "MyBatis",
        description = "Reload MyBatis configuration after configuration create/change.",
        testedVersions = {"All between 5.3.2"},
        expectedVersions = {"5.3.2"},
        supportClass = {
                MyBatisMapperRegister.class,
                MyBatisEntityRegister.class
        }
)
public class MyBatisPlugin {

    private static final Logger logger = Logger.getLogger(MyBatisPlugin.class);

    @OnClassLoadEvent(
            classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration",
            events = {LoadEvent.DEFINE}
    )
    public static void transformPlusConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod removeMappedStatementMethod = CtNewMethod.make("public void $$removeMappedStatement(String statementName) {" +
                "   if(mappedStatements.containsKey(statementName)){" +
                "       mappedStatements.remove(statementName);" +
                "   }" +
                "}", ctClass);
        ctClass.addMethod(removeMappedStatementMethod);
        ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")}).insertBefore("$$removeMappedStatement($1.getId());");
    }

    @OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.session.Configuration")
    public static void transformConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod addMappedStatementMethod = ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")});
        addMappedStatementMethod.setBody(
                "{" +
                        "if(mappedStatements.containsKey($1.getId())){" +
                        "   mappedStatements.remove($1.getId());" +
                        "}" +
                        "mappedStatements.put($1.getId(),$1);" +
                        "}");
        CtMethod addParameterMapMethod = ctClass.getDeclaredMethod("addParameterMap", new CtClass[]{classPool.get("org.apache.ibatis.mapping.ParameterMap")});
        addParameterMapMethod.setBody(
                "{" +
                        "if(parameterMaps.containsKey($1.getId())){" +
                        "   parameterMaps.remove($1.getId());" +
                        "}" +
                        "parameterMaps.put($1.getId(),$1);" +
                        "}");
        CtMethod addResultMapMethod = ctClass.getDeclaredMethod("addResultMap", new CtClass[]{classPool.get("org.apache.ibatis.mapping.ResultMap")});
        addResultMapMethod.setBody(
                "{" +
                        "if(resultMaps.containsKey($1.getId())){" +
                        "   resultMaps.remove($1.getId());" +
                        "}" +
                        "resultMaps.put($1.getId(),$1);" +
                        "checkLocallyForDiscriminatedNestedResultMaps($1);" +
                        "checkGloballyForDiscriminatedNestedResultMaps($1);" +
                        "}");
        CtMethod addKeyGeneratorMethod = ctClass.getDeclaredMethod("addKeyGenerator", new CtClass[]{classPool.get("java.lang.String"), classPool.get("org.apache.ibatis.executor.keygen.KeyGenerator")});
        addKeyGeneratorMethod.setBody(
                "{" +
                        "if(keyGenerators.containsKey($1)){" +
                        "   keyGenerators.remove($1);" +
                        "}" +
                        "keyGenerators.put($1,$2);" +
                        "}");
        CtMethod addCacheMethod = ctClass.getDeclaredMethod("addCache", new CtClass[]{classPool.get("org.apache.ibatis.cache.Cache")});
        addCacheMethod.setBody(
                "{" +
                        "if(caches.containsKey($1.getId())){" +
                        "   caches.remove($1.getId());" +
                        "}" +
                        "caches.put($1.getId(),$1);" +
                        "}");
        for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
            constructor.insertAfter(
                    "{" +
                            "io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisHolder.configuration(this);" +
                            "}");
        }
    }

    // FIXME 开启这个就不加载 org.apache.ibatis.session.Configuration 类了，不知道为啥
    //@OnClassLoadEvent(classNameRegexp = "org.apache.ibatis.session.Configuration\\$StrictMap")
    public static void patchStrictMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put", new CtClass[]{classPool.get(String.class.getName()), classPool.get(Object.class.getName())});
        method.insertBefore(
                "if (containsKey($1)) {" +
                        "   remove($1);" +
                        "}");
    }

    @OnClassLoadEvent(classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration\\$StrictMap")
    public static void patchStrict1Map(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put", new CtClass[]{classPool.get(String.class.getName()), classPool.get(Object.class.getName())});
        method.insertBefore("if(containsKey($1)){remove($1);}");
    }

    /**
     * ClassPathMapperScanner 构造函数插桩，获取ClassPathMapperScanner实例
     */
    @OnClassLoadEvent(classNameRegexp = "org.mybatis.spring.mapper.ClassPathMapperScanner")
    public static void patchMyBatisClassPathMapperScanner(CtClass ctClass, ClassPool classPool) {
        try {
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry")});
            constructor.insertAfter(
                    "{" +
                            "io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisSpringBeanDefinition.loadScanner(this);" +
                            "}");
        } catch (Throwable e) {
            logger.error("patchMyBatisClassPathMapperScanner err", e);
        }
    }

}
