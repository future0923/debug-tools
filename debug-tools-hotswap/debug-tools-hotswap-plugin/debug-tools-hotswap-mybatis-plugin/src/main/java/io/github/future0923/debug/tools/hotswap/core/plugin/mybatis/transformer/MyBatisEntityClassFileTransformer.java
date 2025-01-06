package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisSpringBeanDefinition;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisEntityCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;

import java.lang.annotation.Annotation;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * @author future0923
 */
public class MyBatisEntityClassFileTransformer implements HaClassFileTransformer {

    private static final Logger logger = Logger.getLogger(MyBatisMapperClassFileTransformer.class);

    private final Scheduler scheduler;

    private final String basePackage;

    public MyBatisEntityClassFileTransformer(Scheduler scheduler, String basePackage) {
        this.scheduler = scheduler;
        this.basePackage = basePackage;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        logger.info("transform class: {}", className);
        if (classBeingRedefined == null) {
            logger.error("1");
            return classfileBuffer;
        }
        if (classBeingRedefined.isInterface()) {
            logger.error("2");
            return classfileBuffer;
        }
        if (!isMybatisEntity(loader, classBeingRedefined)) {
            logger.error("3");
            return classfileBuffer;
        }
        if (MyBatisSpringBeanDefinition.getMapperScanner() == null) {
            logger.error("4");
            return classfileBuffer;
        }
        if (MyBatisHolder.getConfiguration().isEmpty()) {
            logger.error("5");
            return classfileBuffer;
        }
        scheduler.scheduleCommand(new MyBatisEntityCommand(loader, classBeingRedefined, MyBatisHolder.getConfiguration().iterator().next()));
        return classfileBuffer;
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return true;
    }

    public static boolean isMybatisEntity(ClassLoader loader, Class<?> clazz) {
        try {
            Arrays.stream(clazz.getAnnotations()).forEach(System.out::println);
            for (Annotation annotation : clazz.getAnnotations()) {
                System.out.println(annotation.getClass().getName());
                if (annotation.annotationType().getName().equals("com.baomidou.mybatisplus.annotation.TableName")) {
                    return true;
                }
            }
            Class<?> modelClass = loader.loadClass("com.baomidou.mybatisplus.extension.activerecord.Model");
            if (modelClass.isAssignableFrom(clazz)) {
                return true;
            }

            // 检查类的父类是否继承 Model
            Class<?> superClass = clazz.getSuperclass();
            while (superClass != null && superClass != Object.class) {
                if (modelClass.isAssignableFrom(superClass)) {
                    return true;
                }
                superClass = superClass.getSuperclass();
            }
        } catch (ClassNotFoundException ignored) {
            logger.error("111", ignored);
        }
        return false;
    }
}