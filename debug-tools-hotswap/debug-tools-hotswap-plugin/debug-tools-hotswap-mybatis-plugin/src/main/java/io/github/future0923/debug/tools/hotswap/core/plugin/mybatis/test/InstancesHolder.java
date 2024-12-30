package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * 实例缓存器
 * 使用步骤：
 * 1. 在 @OnClassLoad函数里面加上 InstancesHolder.insertObjectCacheInConstructor(ctClass);
 * 2. 在需要的时候InstancesHolder.getInstances(class)
 * @author future0923
 */
public class InstancesHolder {

    private static final Logger logger = Logger.getLogger(InstancesHolder.class);

    private static final Map<Class<?>, Set<Object>> instancesHolder = new HashMap<>();
    private static final Set<String> alreadyHold = Collections.synchronizedSet(new HashSet<>());


    /**
     * 根据Class获取对象实例引用
     * @param klass
     * @return
     * @param <T>
     */
    public static <T> Set<T> getInstances(Class<T> klass) {
        Set<T> results = new HashSet<>();
        Set<Object> objects = instancesHolder.get(klass);
        if(null != objects) {
            results.addAll((Set<T>)objects);
        }
        return results;
    }


    /**
     * 插入的是当前类为key
     * @param ctClass
     */
    public static void insertObjectCacheInConstructor(CtClass ctClass) {
        try{
            String className = ctClass.getName();

            //只插桩一次
            if(alreadyHold.contains(className)) {
                return;
            }
            for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
                String src = "{io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.InstancesHolder.insertHolder(this);}";
                constructor.insertAfter(src);
            }

            alreadyHold.add(className);
        }catch (Exception e) {
            logger.error("insertObjectCacheInConstructor err,class={}",e,ctClass.getName());
        }

    }

    /**
     * 插入的是以ctClass为基类的key
     * @param ctClass
     */
    public static void insertObjectCacheInConstructorWithBaseClassKey(CtClass ctClass) {
        try{
            String className = ctClass.getName();

            //只插桩一次
            if(alreadyHold.contains(className)) {
                return;
            }
            for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
                String src = "{io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.test.InstancesHolder.insertHolder(\""+ctClass.getName()+"\",this);}";
                constructor.insertAfter(src);
            }

            alreadyHold.add(className);
        }catch (Exception e) {
            logger.error("insertObjectCacheInConstructor err,class={}",e,ctClass.getName());
        }

    }

    public static void insertHolder(Object obj){
        synchronized (instancesHolder) {
            //使用Weak引用
            Set<Object> objects = instancesHolder.computeIfAbsent(obj.getClass(), k -> Collections.newSetFromMap(new WeakHashMap<>()));
            objects.add(obj);
        }
    }
    public static void insertHolder(String className,Object obj){
        synchronized (instancesHolder) {
            //使用Weak引用
            Set<Object> objects = null;
            try {
                objects = instancesHolder.computeIfAbsent(Class.forName(className),
                        k -> Collections.newSetFromMap(new WeakHashMap<>()));
                objects.add(obj);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}
