package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisSpringBeanDefinition;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisMapperCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;
import org.apache.ibatis.annotations.Mapper;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * 处理mybatis mapper class redefine转换，创建{@link MyBatisMapperCommand}任务
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
        if (classBeingRedefined != null
                && classBeingRedefined.isInterface()
                && classBeingRedefined.getAnnotation(Mapper.class) != null
                && MyBatisSpringBeanDefinition.getMapperScanner() != null
                && !MyBatisHolder.getConfiguration().isEmpty()) {
            logger.debug("transform class: {}", className);
            scheduler.scheduleCommand(new MyBatisMapperCommand(classBeingRedefined, classfileBuffer, MyBatisSpringBeanDefinition.getMapperScanner(), MyBatisHolder.getConfiguration().iterator().next()));
        }
        return classfileBuffer;
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return true;
    }
}
