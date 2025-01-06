package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register;

import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer.MyBatisMapperClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取{@link MapperScan}的mapper路径注册{@link MyBatisMapperClassFileTransformer}进行重载
 *
 * @author future0923
 */
public class MyBatisMapperRegister {

    @Init
    static HotswapTransformer hotswapTransformer;

    @Init
    static ClassLoader appClassLoader;

    @Init
    static Scheduler scheduler;

    @OnClassLoadEvent(classNameRegexp = "org.mybatis.spring.annotation.MapperScannerRegistrar")
    public static void registerBasePackage(CtClass ctClass, ClassPool classPool) {
        try {
            CtMethod registerBeanDefinitions = ctClass.getDeclaredMethod("registerBeanDefinitions", new CtClass[]{classPool.get("org.springframework.core.type.AnnotationMetadata"), classPool.get("org.springframework.core.annotation.AnnotationAttributes"), classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry"), classPool.get("java.lang.String")});
            registerBeanDefinitions.insertAfter("{" +
                    "io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.register.MyBatisMapperRegister.basePackage($1, $2);" +
                    "}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void basePackage(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs) {
        List<String> basePackages = new ArrayList<>();
        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
        basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
        basePackages.addAll(Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(annoMeta.getClassName()));
        }
        for (String basePackage : basePackages) {
            registerBasePackage(basePackage);
        }
    }

    /**
     * 创建对应包下变动的{@link MyBatisMapperClassFileTransformer}，可以处理class的redefine事件
     */
    public static void registerBasePackage(final String basePackage) {
        String classNameRegExp = DebugToolsStringUtils.getClassNameRegExp(basePackage);
        hotswapTransformer.registerTransformer(appClassLoader, classNameRegExp, new MyBatisMapperClassFileTransformer(scheduler, classNameRegExp));
    }
}
