package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register;

import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer.MyBatisEntityClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;

/**
 * @author future0923
 */
public class MyBatisEntityRegister {

    @Init
    static HotswapTransformer hotswapTransformer;

    @Init
    static ClassLoader appClassLoader;

    @Init
    static Scheduler scheduler;
    //
    //@OnClassLoadEvent(classNameRegexp = "io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent")
    //public static void registerBasePackage(CtClass ctClass, ClassPool classPool) {
    //    try {
    //        CtMethod registerBeanDefinitions = ctClass.getDeclaredMethod("registerBasePackage", new CtClass[]{classPool.get("java.lang.String")});
    //        registerBeanDefinitions.insertAfter("{" +
    //                "io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MyBatisEntityRegister.basePackage($1);" +
    //                "}");
    //    } catch (Exception e) {
    //        throw new RuntimeException(e);
    //    }
    //}

    @OnClassLoadEvent(classNameRegexp = "org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider")
    public static void transform(CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod method = clazz.getDeclaredMethod("findCandidateComponents", new CtClass[]{classPool.get("java.lang.String")});
        method.insertAfter(
                "if (this instanceof org.springframework.context.annotation.ClassPathBeanDefinitionScanner) {" +
                        "io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MyBatisEntityRegister.basePackage($1);" +
                    "}");

    }

    /**
     * 创建对应包下变动的{@link MyBatisEntityClassFileTransformer}，可以处理class的redefine事件
     */
    public static void basePackage(String basePackage) {
        String classNameRegExp = DebugToolsStringUtils.getClassNameRegExp(basePackage);
        hotswapTransformer.registerTransformer(appClassLoader, classNameRegExp, new MyBatisEntityClassFileTransformer(scheduler, classNameRegExp));
    }
}
