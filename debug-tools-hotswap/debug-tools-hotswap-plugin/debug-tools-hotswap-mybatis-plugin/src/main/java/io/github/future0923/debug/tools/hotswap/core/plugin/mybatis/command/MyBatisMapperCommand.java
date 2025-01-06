package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean.MyBatisSpringBeanDefinition;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

/**
 * 重载 MyBatis Mapper 命令
 *
 * @author future0923
 */
public class MyBatisMapperCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(MyBatisMapperCommand.class);

    private final Class<?> clazz;

    private final byte[] bytes;
    private final ClassPathMapperScanner mapperScanner;
    private final Configuration configuration;

    public MyBatisMapperCommand(Class<?> clazz, byte[] bytes, ClassPathMapperScanner mapperScanner, Configuration configuration) {
        this.clazz = clazz;
        this.bytes = bytes;
        this.mapperScanner = mapperScanner;
        this.configuration = configuration;
    }

    @Override
    public void executeCommand() {
        try {
            Class<? extends Configuration> configurationClass = configuration.getClass();
            if (configurationClass.getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                ReflectionHelper.invoke(configuration, configurationClass, "removeMapper", new Class[]{Class.class}, clazz);
                ReflectionHelper.invoke(configuration, configurationClass, "addMapper", new Class[]{Class.class}, clazz);
            }
            ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(mapperScanner);
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(bytes);
            if (beanDefinition == null) {
                logger.error("没有找到beanDefinition:{}", clazz.getName());
                return;
            }
            scannerAgent.defineBean(beanDefinition);
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(mapperScanner, "beanNameGenerator");
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ReflectionHelper.get(scannerAgent, "registry");
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            MyBatisSpringBeanDefinition.mybatisBeanDefinition(definitionHolder);
            logger.reload("reload :{}", clazz.getName());
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
