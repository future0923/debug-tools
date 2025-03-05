package io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer;


import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanRefreshCommand;
import io.github.future0923.debug.tools.hotswap.core.util.HaClassFileTransformer;

import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

/**
 * SpringBean类文件Transformer，处理修改的class，即redefine类型的class
 *
 * @author future0923
 */
public class SpringBeanClassFileTransformer implements HaClassFileTransformer {

    private static final Logger logger = Logger.getLogger(SpringBeanClassFileTransformer.class);

    private final ClassLoader appClassLoader;
    private final Scheduler scheduler;
    private final String basePackage;

    public SpringBeanClassFileTransformer(ClassLoader appClassLoader, Scheduler scheduler, String basePackage) {
        this.appClassLoader = appClassLoader;
        this.scheduler = scheduler;
        this.basePackage = basePackage;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (classBeingRedefined != null) {
            final SpringChangesAnalyzer analyzer = new SpringChangesAnalyzer(appClassLoader);
            className = className.replace("/", ".");
            if (analyzer.isReloadNeeded(classBeingRedefined, classfileBuffer)) {
                logger.info("watch change class event, start reloading spring bean, class name:{}, classfileBuffer:{}", className, classfileBuffer);
                scheduler.scheduleCommand(new ClassPathBeanRefreshCommand(classBeingRedefined.getClassLoader(), basePackage, className, classfileBuffer));
            } else {
                logger.debug("watch change class event, There is no need to reload Spring beans, className:{}", className);
            }
        }
        return classfileBuffer;
    }

    @Override
    public boolean isForRedefinitionOnly() {
        return true;
    }
}
