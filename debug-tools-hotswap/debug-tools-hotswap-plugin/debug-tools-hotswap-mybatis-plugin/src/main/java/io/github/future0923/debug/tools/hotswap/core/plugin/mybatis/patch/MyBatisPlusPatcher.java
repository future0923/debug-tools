package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusEntityReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusMapperReloadCommand;

/**
 * @author future0923
 */
public class MyBatisPlusPatcher {

    private static final Logger logger = Logger.getLogger(MyBatisPlusPatcher.class);

    @Init
    static Scheduler scheduler;

    @Init
    static ClassLoader appClassLoader;

    @OnClassLoadEvent(classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration")
    public static void patchMybatisConfiguration(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod removeMappedStatementMethod = CtNewMethod.make("public void $$removeMappedStatement(String statementName) {" +
                "   if(mappedStatements.containsKey(statementName)){" +
                "       mappedStatements.remove(statementName);" +
                "   }" +
                "}", ctClass);
        ctClass.addMethod(removeMappedStatementMethod);
        CtMethod addMappedStatement = ctClass.getDeclaredMethod("addMappedStatement", new CtClass[]{classPool.get("org.apache.ibatis.mapping.MappedStatement")});
        addMappedStatement.insertBefore("$$removeMappedStatement($1.getId());");
    }


    /**
     * 实际上是 com.baomidou.mybatisplus.core.MybatisConfiguration$StrictMap，将 $ 换成 . 也可以识别
     * 写 $ 时 com.baomidou.mybatisplus.core.MybatisConfiguration 主类就获取不到了，不知道为啥
     */
    @OnClassLoadEvent(classNameRegexp = "com.baomidou.mybatisplus.core.MybatisConfiguration.StrictMap")
    public static void patchMybatisConfigurationStrictMap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put", new CtClass[]{classPool.get(String.class.getName()), classPool.get(Object.class.getName())});
        method.insertBefore("if(containsKey($1)){" +
                "   remove($1);" +
                "}");
    }

    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void redefineMyBatisPlus(final Class<?> clazz, final byte[] bytes) {
        logger.debug("redefineMyBatisPlus, className:{}", clazz.getName());
        scheduler.scheduleCommand(new MyBatisPlusEntityReloadCommand(appClassLoader, clazz), 500);
        scheduler.scheduleCommand(new MyBatisPlusMapperReloadCommand(appClassLoader, clazz, bytes), 500);
    }
}
