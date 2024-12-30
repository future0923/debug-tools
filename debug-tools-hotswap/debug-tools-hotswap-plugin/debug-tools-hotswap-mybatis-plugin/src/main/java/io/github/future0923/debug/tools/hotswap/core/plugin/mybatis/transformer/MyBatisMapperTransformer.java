package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.transformer;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.InstancesHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.MyBatisSpringBeanDefinition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import java.util.Set;

/**
 * @author future0923
 */
public class MyBatisMapperTransformer {

    private static final Logger logger = Logger.getLogger(MyBatisMapperTransformer.class);

    /**
     * ClassPathMapperScanner 构造函数插桩，获取ClassPathMapperScanner实例
     */
    @OnClassLoadEvent(classNameRegexp = "org.mybatis.spring.mapper.ClassPathMapperScanner")
    public static void patchMyBatisClassPathMapperScanner(CtClass ctClass, ClassPool classPool) {
        logger.info("MyBatisBeanRefresh.patchMyBatisClassPathMapperScanner");
        try {
            CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{
                    classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry")});
            constructor.insertAfter("{io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.MyBatisSpringBeanDefinition.loadScanner(this);}");
        } catch (Throwable e) {
            logger.error("patchMyBatisClassPathMapperScanner err", e);
        }
    }

    @OnClassLoadEvent(classNameRegexp = "io.github.future0923.debug.tools.test.application.dao.UserDao")
    public static void myBatisBeanRefresh(ClassLoader classLoader, Class<?> clazz, byte[] bytes) {
        logger.error("myBatisBeanRefresh, {}", clazz);
        if (clazz == null || !clazz.isInterface()) {
            return;
        }
        if (clazz.getAnnotation(Mapper.class) == null) {
            return;
        }
        if (MyBatisSpringBeanDefinition.getMapperScanner() == null) {
            return;
        }
        Set<Configuration> configurations = InstancesHolder.getInstances(Configuration.class);
        if(configurations.isEmpty()){
            return;
        }
        try {
            Configuration configuration = configurations.iterator().next();
            //这里用类字符串判断是否mybatis plus，不引用mybatis plus的类，避免应用程序没有用mybatis plus而报错
            if(configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                // MyBatis Plus 刷新mapper接口，可支持新增mapper接口
                if(!(configuration instanceof MybatisConfiguration)) {
                    return;
                }
                MybatisConfiguration plugConfiguration = (MybatisConfiguration)configuration;
                plugConfiguration.addNewMapper(clazz);
                //plugConfiguration.addMapper(clazz);
            }

            ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(MyBatisSpringBeanDefinition.getMapperScanner());
            BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(bytes);
            if (beanDefinition != null) {
                scannerAgent.defineBean(beanDefinition);
            }

            //bean name
            BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(MyBatisSpringBeanDefinition.getMapperScanner(), "beanNameGenerator");
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ReflectionHelper.get(scannerAgent,"registry");
            String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);

            //beanDefinitionHolder
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
            MyBatisSpringBeanDefinition.mybatisBeanDefinition(definitionHolder);
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
