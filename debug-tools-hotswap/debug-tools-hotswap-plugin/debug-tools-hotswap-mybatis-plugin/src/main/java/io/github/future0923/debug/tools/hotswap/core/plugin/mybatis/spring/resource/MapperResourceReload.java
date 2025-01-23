package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.spring.resource;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.session.Configuration;

import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * 重新载入 mybatis 的 mapper 资源
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MapperResourceReload extends AbstractMyBatisResourceReload {

    private static final Logger logger = Logger.getLogger(MapperResourceReload.class);

    private MapperRegistry mapperRegistry;

    private Class<?> mapperClass;

    public MapperResourceReload(String loadedResource, Configuration configuration) {
        this.type = MyBatisResourceReload.INTERFACE_TYPE;
        this.loadedResource = loadedResource;
        this.configuration = configuration;
        try {
            this.mapperRegistry = (MapperRegistry) ReflectionHelper.get(configuration, "mapperRegistry");
            this.loadedResources = (Set<String>) ReflectionHelper.get(configuration, LOADED_RESOURCES_FIELD);
            Map<Class<?>, MapperProxyFactory<?>> knownMapperMap = (Map<Class<?>, MapperProxyFactory<?>>) ReflectionHelper.get(this.mapperRegistry, "knownMappers");
            for (Class<?> mapperClass : knownMapperMap.keySet()) {
                if (loadedResource.contains(mapperClass.getName())) {
                    this.mapperClass = mapperClass;
                }
            }
        } catch (Exception e) {
            logger.error("获取 MybatisConfiguration loadedResources失败！", e, new Object[0]);
        }
    }

    public void removeLoadedMark() throws Exception {
        removeLoadedResource();
        Map<Class<?>, MapperProxyFactory<?>> knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) ReflectionHelper.get(mapperRegistry, "knownMappers");
        knownMappers.remove(mapperClass);
    }

    public void reloadAnnotation(Class<?> redefineClass) {
        new MapperAnnotationBuilder(configuration, redefineClass).parse();
    }

    @Override
    public void reload(URL url) throws Exception {

    }
}
