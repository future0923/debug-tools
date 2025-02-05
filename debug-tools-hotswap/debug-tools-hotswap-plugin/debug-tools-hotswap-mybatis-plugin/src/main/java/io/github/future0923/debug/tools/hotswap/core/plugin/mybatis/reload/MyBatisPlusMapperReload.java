package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.dto.MyBatisPlusMapperReloadDTO;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 重载 MyBatisPlus Mapper 资源
 *
 * @author future0923
 */
public class MyBatisPlusMapperReload extends AbstractMyBatisResourceReload<MyBatisPlusMapperReloadDTO> {

    private static final Logger logger = Logger.getLogger(MyBatisPlusMapperReload.class);

    public static final MyBatisPlusMapperReload INSTANCE = new MyBatisPlusMapperReload();

    private MyBatisPlusMapperReload() {
    }

    @Override
    protected void doReload(MyBatisPlusMapperReloadDTO dto) throws Exception {
        Class<?> clazz = dto.getClazz();
        try {
            logger.debug("transform class: {}", clazz.getName());
            if (!clazz.isInterface()) {
                logger.debug("classBeingRedefined is not isInterface");
                return;
            }
            if (!isMybatisMapper(dto.getAppClassLoader(), clazz)) {
                logger.debug("classBeingRedefined is not mapper");
                return;
            }
            ClassPathMapperScanner mapperScanner = MyBatisSpringResourceManager.getMapperScanner();
            if (mapperScanner == null) {
                logger.debug("mapperScanner is null");
                return;
            }
            Set<Configuration> configurationList = MyBatisSpringResourceManager.getConfigurationList();
            if (configurationList.isEmpty()) {
                logger.debug("mybatis configuration is empty");
                return;
            }
            for (Configuration configuration : configurationList) {
                Class<? extends Configuration> configurationClass = configuration.getClass();
                if (configurationClass.getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                    ReflectionHelper.invoke(configuration, configurationClass, "removeMapper", new Class[]{Class.class}, clazz);
                    ReflectionHelper.invoke(configuration, configurationClass, "addMapper", new Class[]{Class.class}, clazz);
                    ClassPathBeanDefinitionScannerAgent scannerAgent = ClassPathBeanDefinitionScannerAgent.getInstance(mapperScanner);
                    BeanDefinition beanDefinition = scannerAgent.resolveBeanDefinition(dto.getBytes());
                    if (beanDefinition == null) {
                        logger.error("没有找到beanDefinition:{}", clazz.getName());
                        return;
                    }
                    scannerAgent.defineBean(beanDefinition);
                    BeanNameGenerator beanNameGenerator = (BeanNameGenerator) ReflectionHelper.get(mapperScanner, "beanNameGenerator");
                    BeanDefinitionRegistry registry = (BeanDefinitionRegistry) ReflectionHelper.get(scannerAgent, "registry");
                    String beanName = beanNameGenerator.generateBeanName(beanDefinition, registry);
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(beanDefinition, beanName);
                    mybatisBeanDefinition(mapperScanner, definitionHolder);
                    logger.reload("reload {} in {}", clazz.getName(), configuration);
                }
            }
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }

    /**
     * <p>目前识别方式</p>
     * <ul>
     *     <li>有{@link Mapper}注解</li>
     *     <li>继承{@code BaseMapper}</li>
     * </ul>
     */
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

    /**
     * 这块是mybatis接口的生成代理类的原理
     */
    public static void mybatisBeanDefinition(ClassPathMapperScanner mapperScanner, BeanDefinitionHolder holder){
        try{
            Set<BeanDefinitionHolder> holders = new HashSet<>();
            holders.add(holder);
            Class<?> classPathMapperScanner = Class.forName("org.mybatis.spring.mapper.ClassPathMapperScanner");
            Method method = classPathMapperScanner.getDeclaredMethod("processBeanDefinitions", Set.class);
            boolean isAccess = method.isAccessible();
            method.setAccessible(true);
            method.invoke(mapperScanner, holders);
            method.setAccessible(isAccess);
        }catch (Exception e) {
            logger.error("freshMyBatis err",e);
        }
    }
}
