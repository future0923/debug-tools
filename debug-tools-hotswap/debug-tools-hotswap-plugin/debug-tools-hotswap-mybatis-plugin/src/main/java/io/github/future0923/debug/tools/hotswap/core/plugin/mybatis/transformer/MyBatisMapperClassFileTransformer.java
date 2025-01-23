package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisSpringBeanDefinition;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisMapperCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.ClassPathMapperScanner;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 处理mybatis mapper class redefine转换，创建{@link MyBatisMapperCommand}任务
 *
 * <p>目前识别方式</p>
 * <ul>
 *     <li>有{@link Mapper}注解</li>
 *     <li>继承{@code BaseMapper}</li>
 * </ul>
 *
 * @author future0923
 */
public class MyBatisMapperClassFileTransformer implements HaClassFileTransformer {

    private static final Logger logger = Logger.getLogger(MyBatisMapperClassFileTransformer.class);

    private final Scheduler scheduler;

    private final String basePackage;

    public MyBatisMapperClassFileTransformer(Scheduler scheduler, String basePackage) {
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
        if (!classBeingRedefined.isInterface()) {
            logger.debug("classBeingRedefined is not isInterface");
            return classfileBuffer;
        }
        if (!isMybatisMapper(loader, classBeingRedefined)) {
            logger.debug("classBeingRedefined is not mapper");
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
        scheduler.scheduleCommand(new MyBatisMapperCommand(classBeingRedefined, classfileBuffer, mapperScanner, MyBatisHolder.getConfiguration().iterator().next()));
        return classfileBuffer;
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return true;
    }

    public static boolean isMybatisMapper(ClassLoader loader, Class<?> clazz) {
        try {
            if (clazz.getAnnotation(Mapper.class) != null) {
                return true;
            }
            Class<?> baseMapperClass = loader.loadClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
            if (baseMapperClass.isAssignableFrom(clazz)) {
                return true;
            }
            // 检查类的父类是否继承 BaseMapper
            Class<?> superClass = clazz.getSuperclass();
            while (superClass != null && superClass != Object.class) {
                if (baseMapperClass.isAssignableFrom(superClass)) {
                    return true;
                }
                superClass = superClass.getSuperclass();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return false;
    }
}
