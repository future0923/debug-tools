package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.utils;

import io.github.future0923.debug.tools.base.logging.Logger;
import org.apache.ibatis.annotations.Mapper;

import java.lang.annotation.Annotation;

/**
 * @author future0923
 */
public class MyBatisUtils {

    private static final Logger logger = Logger.getLogger(MyBatisUtils.class);

    /**
     * <p>目前识别方式</p>
     * <ul>
     *     <li>有com.baomidou.mybatisplus.annotation.TableName注解</li>
     *     <li>继承或父类继承com.baomidou.mybatisplus.extension.activerecord.Model</li>
     * </ul>
     */
    public static boolean isMyBatisPlusEntity(ClassLoader loader, Class<?> clazz) {
        try {
            if (clazz.isInterface()) {
                logger.debug("classBeingRedefined is interface");
                return false;
            }
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

    /**
     * <p>目前识别方式</p>
     * <ul>
     *     <li>有{@link Mapper}注解</li>
     *     <li>继承{@code BaseMapper}</li>
     * </ul>
     */
    public static boolean isMyBatisMapper(ClassLoader loader, Class<?> clazz) {
        try {
            if (!clazz.isInterface()) {
                logger.debug("classBeingRedefined is not isInterface");
                return false;
            }
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
