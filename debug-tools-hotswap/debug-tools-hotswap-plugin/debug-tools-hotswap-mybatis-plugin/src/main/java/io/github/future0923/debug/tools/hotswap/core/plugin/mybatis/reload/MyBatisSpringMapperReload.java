package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.session.Configuration;

import java.util.Map;
import java.util.Set;

/**
 * 重新载入 mybatis spring 的 mapper 资源
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MyBatisSpringMapperReload extends AbstractMyBatisResourceReload<String> {

    private static final Logger logger = Logger.getLogger(MyBatisSpringMapperReload.class);

    public static final MyBatisSpringMapperReload INSTANCE = new MyBatisSpringMapperReload();

    private MyBatisSpringMapperReload() {

    }

    @Override
    protected void doReload(String className) throws Exception {
        String loadedResource = buildLoadedResource(className);
        for (Configuration configuration : MyBatisSpringResourceManager.getConfigurationList()) {
            if (configuration.getClass().getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                continue;
            }
            Set<String> loadedResources = (Set<String>) ReflectionHelper.get(configuration, LOADED_RESOURCES_FIELD);
            if (!loadedResources.contains(loadedResource)) {
                continue;
            }
            loadedResources.remove(loadedResource);
            MapperRegistry mapperRegistry = (MapperRegistry) ReflectionHelper.get(configuration, "mapperRegistry");
            Map<Class<?>, MapperProxyFactory<?>> knownMappers = (Map<Class<?>, MapperProxyFactory<?>>) ReflectionHelper.get(mapperRegistry, "knownMappers");
            knownMappers.keySet().removeIf(mapperClass -> loadedResource.contains(mapperClass.getName()));
            new MapperAnnotationBuilder(configuration, Class.forName(className)).parse();
            logger.reload("reload MyBatis mapper class {}", className);
        }
    }

    private String buildLoadedResource(String className) {
        return INTERFACE + " " + className;
    }
}
