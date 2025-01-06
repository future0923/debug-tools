package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.bean;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.MyBatisPlugin;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * mybatis的生成代理对象的定义注入到spring中
 *
 * @author future0923
 */
public class MyBatisSpringBeanDefinition {

    private static final Logger logger = Logger.getLogger(MyBatisSpringBeanDefinition.class);

    private static ClassPathMapperScanner mapperScanner;

    /**
     * {@link MyBatisPlugin#patchMyBatisClassPathMapperScanner}注入对象
     */
    public static void loadScanner(ClassPathMapperScanner scanner) {
        if(null != mapperScanner) {
            return;
        }
        mapperScanner = scanner;

    }

    public static ClassPathMapperScanner getMapperScanner() {
        return mapperScanner;
    }

    /**
     * 这块是mybatis接口的生成代理类的原理
     */
    public static void mybatisBeanDefinition(BeanDefinitionHolder holder){
        if(null == mapperScanner) {
            return;
        }
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
