package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisSpringBeanDefinition;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisEntityCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisMapperCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;
import org.mybatis.spring.mapper.ClassPathMapperScanner;

import java.lang.annotation.Annotation;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;

/**
 * 处理mybatis entity class redefine转换，创建{@link MyBatisMapperCommand}任务。
 *
 * <p>目前识别方式</p>
 * <ul>
 *     <li>有com.baomidou.mybatisplus.annotation.TableName注解</li>
 *     <li>继承或父类继承com.baomidou.mybatisplus.extension.activerecord.Model</li>
 * </ul>
 *
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
        logger.debug("transform class: {}", className);
        if (classBeingRedefined == null) {
            logger.debug("classBeingRedefined is null");
            return classfileBuffer;
        }
        if (classBeingRedefined.isInterface()) {
            logger.debug("classBeingRedefined is interface");
            return classfileBuffer;
        }
        if (!isMybatisEntity(loader, classBeingRedefined)) {
            logger.debug("classBeingRedefined is not mybatis entity");
            return classfileBuffer;
        }
        ClassPathMapperScanner mapperScanner = MyBatisSpringBeanDefinition.getMapperScanner();
        if (mapperScanner == null) {
            logger.debug("mapperScanner is null");
            return classfileBuffer;
        }
        if (MyBatisHolder.getConfiguration().isEmpty()) {
            logger.debug("mybatis configuration is empty");
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
            for (Annotation annotation : clazz.getAnnotations()) {
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
        }
        return false;
    }
}