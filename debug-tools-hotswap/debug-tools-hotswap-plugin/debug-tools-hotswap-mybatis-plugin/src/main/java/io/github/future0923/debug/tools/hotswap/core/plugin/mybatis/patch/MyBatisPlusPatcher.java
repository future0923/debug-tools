package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch;

import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtNewMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;

/**
 * @author future0923
 */
public class MyBatisPlusPatcher {

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
    public static void patchStrict1Map(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("put", new CtClass[]{classPool.get(String.class.getName()), classPool.get(Object.class.getName())});
        method.insertBefore("if(containsKey($1)){" +
                "   remove($1);" +
                "}");
    }
}
