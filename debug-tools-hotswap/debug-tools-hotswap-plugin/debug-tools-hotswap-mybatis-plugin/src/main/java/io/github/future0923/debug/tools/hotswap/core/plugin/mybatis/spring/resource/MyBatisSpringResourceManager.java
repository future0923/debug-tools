package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.spring.resource;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.patch.MyBatisSpringPatcher;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MyBatisSpringResourceManager {

    private static final Logger logger = Logger.getLogger(MyBatisSpringResourceManager.class);

    /**
     * mybatis Configuration 集合
     */
    private static final List<Configuration> configurationList = new LinkedList<>();

    private static final Map<String, List<XmlResourceReload>> xmlResourceMap = new ConcurrentHashMap<>();

    private static final Map<String, List<MapperResourceReload>> annotationResourceMap = new ConcurrentHashMap<>();

    /**
     * 当{@link SqlSessionFactoryBean}实例化完成时会获取到{@link Configuration}对象注入到集合中，在{@link MyBatisSpringPatcher#patchSqlSessionFactoryBean(CtClass, ClassPool)}插桩
     */
    public static void registerConfiguration(Configuration configuration) {
        if (configuration != null) {
            configurationList.add(configuration);
        }
    }

    /**
     * 当Spring应用上下文初始化完成时会调用这里，在{@link MyBatisSpringPatcher#patchAbstractApplicationContext(CtClass, ClassPool)}插桩
     */
    public static void init() {
        if (!configurationList.isEmpty()) {
            for (Configuration configuration : configurationList) {
                Set<String> loadedResources = (Set<String>) ReflectionHelper.get(configuration, MyBatisResourceReload.LOADED_RESOURCES_FIELD);
                if (loadedResources != null && !loadedResources.isEmpty()) {
                    for (String loadedResource : loadedResources) {
                        try {
                            if (loadedResource.trim().startsWith(MyBatisResourceReload.INTERFACE)) {
                                MapperResourceReload annotationResource = new MapperResourceReload(loadedResource, configuration);
                                List<MapperResourceReload> annotationResources = annotationResourceMap.get(loadedResource);
                                if (annotationResources == null) {
                                    annotationResourceMap.put(loadedResource, new ArrayList<MapperResourceReload>() {{
                                        add(annotationResource);
                                    }});
                                } else {
                                    // 判断当前 configuration 中是否有该 interface 资源，没有就加进去
                                    if (!containsAnnotationConfiguration(annotationResources, configuration)) {
                                        annotationResources.add(annotationResource);
                                    }
                                }
                            } else {
                                XmlResourceReload xmlResource = new XmlResourceReload(loadedResource, configuration);
                                List<XmlResourceReload> xmlResources = xmlResourceMap.get(loadedResource);
                                if (xmlResources == null) {
                                    xmlResourceMap.put(loadedResource, new ArrayList<XmlResourceReload>() {{
                                        add(xmlResource);
                                    }});
                                } else {
                                    // 判断当前 configuration 中是否有该 xml 资源，没有就加进去
                                    if (!containsXmlConfiguration(xmlResources, configuration)) {
                                        xmlResources.add(xmlResource);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            logger.error("Mybatis resource init error", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 判断当前 configuration 中是否有该 interface 资源，没有就加进去
     */
    public static boolean containsAnnotationConfiguration(List<MapperResourceReload> annotationResources, Configuration current) {
        if (annotationResources == null || annotationResources.isEmpty()) {
            return false;
        }
        for (AbstractMyBatisResourceReload abstractMybatisResource : annotationResources) {
            if (abstractMybatisResource.getConfiguration().equals(current)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前 configuration 中是否有该资源
     */
    public static boolean containsXmlConfiguration(List<XmlResourceReload> xmlResources, Configuration current) {
        if (xmlResources == null || xmlResources.isEmpty()) {
            return false;
        }
        for (AbstractMyBatisResourceReload abstractMybatisResource : xmlResources) {
            if (abstractMybatisResource.getConfiguration() == current) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通过 URL 信息找到所有的 XmlResourceReload
     */
    public static List<XmlResourceReload> findXmlResource(URL url) {
        if (url != null) {
            String relativePath = getRelativePath(url);
            for (Map.Entry<String, List<XmlResourceReload>> entry : xmlResourceMap.entrySet()) {
                if (entry.getKey().contains(relativePath)) {
                    return entry.getValue();
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("xmlResourcePath:");

            for (String xmlPath : xmlResourceMap.keySet()) {
                stringBuilder.append(xmlPath).append(";");
            }

            logger.info(stringBuilder.toString());
        }
        return null;
    }

    /**
     * 获取 url 的真实地址，因为可能在 watchResources 和 extraClasspath 中
     */
    private static String getRelativePath(URL changedUrl) {
        PluginConfiguration pluginConfiguration = PluginManager.getInstance().getPluginConfiguration(MyBatisSpringResourceManager.class.getClassLoader());
        String changePath = changedUrl.getPath();
        URL[] watchResources = pluginConfiguration.getWatchResources();
        if (watchResources != null) {
            for (URL watchResource : watchResources) {
                if (changePath.contains(watchResource.getPath())) {
                    return changePath.replace(watchResource.getPath(), "");
                }
            }
        }

        URL[] extraClasspath = pluginConfiguration.getExtraClasspath();
        if (extraClasspath != null) {
            for (URL extraUrl : extraClasspath) {
                if (changePath.contains(extraUrl.getPath())) {
                    return changePath.replace(extraUrl.getPath(), "");
                }
            }
        }

        return changePath;
    }

    public static List<MapperResourceReload> findAnnotationResource(String className) {
        logger.info("MyBatisSpringResourceManager classLoader:{}, MyBatisResourceReload classLoader:{}", MyBatisSpringResourceManager.class.getClassLoader(), MapperResourceReload.class.getClassLoader());
        if (className == null) {
            return null;
        } else {
            for (Map.Entry<String, List<MapperResourceReload>> entry : annotationResourceMap.entrySet()) {
                if (entry.getKey().endsWith(className)) {
                    return entry.getValue();
                }
            }
            return null;
        }
    }

    public static void removeMybatisCache(String className) {
        if (className != null) {
            logger.info("准备删除 MybatisConfiguration 缓存，className:{}", className);
            List<MapperResourceReload> annotationResources = findAnnotationResource(className);
            if (annotationResources == null) {
                logger.warning("无法找到 Mybatis 对应的资源！className：{}, allPath:{}", className, annotationResourceMap.keySet());
            } else {
                try {
                    for (MapperResourceReload annotationResource : annotationResources) {
                        annotationResource.removeLoadedMark();
                    }
                } catch (Exception e) {
                    logger.error("删除 Mybatis Configuration Cache 失败！", e);
                }
            }
        }
    }
}
