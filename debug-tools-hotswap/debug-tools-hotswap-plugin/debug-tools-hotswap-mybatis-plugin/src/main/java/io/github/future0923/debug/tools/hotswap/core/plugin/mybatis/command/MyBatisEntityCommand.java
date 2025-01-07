package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.Reflector;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 重载 MyBatis Entity 命令 AbstractSqlInjector.inspectInject
 *
 * @author future0923
 */
@SuppressWarnings("unchecked")
public class MyBatisEntityCommand extends MergeableCommand {

    private static final Logger logger = Logger.getLogger(MyBatisEntityCommand.class);

    private final ClassLoader classLoader;

    private final Class<?> clazz;

    private final Configuration configuration;

    public MyBatisEntityCommand(ClassLoader classLoader, Class<?> clazz, Configuration configuration) {
        this.classLoader = classLoader;
        this.clazz = clazz;
        this.configuration = configuration;
    }

    @Override
    public void executeCommand() {
        try {
            Class<? extends Configuration> configurationClass = configuration.getClass();
            if (configurationClass.getName().equals("com.baomidou.mybatisplus.core.MybatisConfiguration")) {
                MapperRegistry mapperRegistry = configuration.getMapperRegistry();
                Collection<Class<?>> mappers = mapperRegistry.getMappers();
                List<Class<?>> mapperClassList = new LinkedList<>();
                for (Class<?> mapper : mappers) {
                    Type[] genericInterfaces = mapper.getGenericInterfaces();
                    for (Type genericInterface : genericInterfaces) {
                        if (genericInterface instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                            if (actualTypeArguments.length > 0) {
                                Type modelType = Arrays.stream(actualTypeArguments).filter(type -> type.getTypeName().equals(clazz.getTypeName())).findAny().orElse(null);
                                if (modelType != null) {
                                    mapperClassList.add(mapper);
                                    break;
                                }
                            }
                        }
                    }
                }
                for (Class<?> mapperClass : mapperClassList) {
                    Map<String, MappedStatement> mappedStatements = (Map<String, MappedStatement>) ReflectionHelper.get(configuration, "mappedStatements");
                    // 清空 Mapper 方法 mappedStatement 缓存信息
                    final String typeKey = mapperClass.getName() + ".";
                    Set<String> mapperSet = mappedStatements.keySet()
                            .stream()
                            .filter(mappedStatement -> mappedStatement.startsWith(typeKey))
                            .collect(Collectors.toSet());
                    for (String key : mapperSet) {
                        mappedStatements.remove(key);
                    }

                    //构建MapperBuilderAssistant
                    String xmlResource = mapperClass.getName().replace(".", "/") + ".java (best guess)";
                    MapperBuilderAssistant builderAssistant = new MapperBuilderAssistant(configuration, xmlResource);
                    builderAssistant.setCurrentNamespace(mapperClass.getName());

                    //移除实体类对应的字段缓存，否则在初始化TableInfo的时候，不重新初始化字段集合
                    Map<Class<?>, List<Field>> classFieldCache = (Map<Class<?>, List<Field>>) ReflectionHelper.get(null, classLoader.loadClass("com.baomidou.mybatisplus.core.toolkit.ReflectionKit"), "CLASS_FIELD_CACHE");
                    classFieldCache.remove(clazz);

                    //移除mapper缓存，否则不执行循环注入自定义方法  if (!mapperRegistryCache.contains(className)) {
                    Set<String> mapperRegistryCache = (Set<String>) ReflectionHelper.invoke(null, classLoader.loadClass("com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils"), "getMapperRegistryCache", new Class[]{Configuration.class}, builderAssistant.getConfiguration());
                    mapperRegistryCache.remove(mapperClass.toString());

                    //移除实体类对应的表缓存，否则不重新初始化TableInfo
                    ReflectionHelper.invoke(null, classLoader.loadClass("com.baomidou.mybatisplus.core.metadata.TableInfoHelper"), "remove", new Class[]{Class.class}, clazz);

                    //移除实体类对应的映射器缓存
                    DefaultReflectorFactory reflectorFactory = (DefaultReflectorFactory) configuration.getReflectorFactory();
                    ConcurrentMap<Class<?>, Reflector> reflectorMap = (ConcurrentMap<Class<?>, Reflector>) ReflectionHelper.get(reflectorFactory, reflectorFactory.getClass(), "reflectorMap");
                    reflectorMap.remove(clazz);

                    //注入自定义方法
                    Object iSqlInjector = ReflectionHelper.invoke(null, classLoader.loadClass("com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils"), "getSqlInjector", new Class[]{Configuration.class}, configuration);
                    ReflectionHelper.invoke(iSqlInjector, iSqlInjector.getClass(), "inspectInject", new Class[]{MapperBuilderAssistant.class, Class.class}, builderAssistant, mapperClass);
                }
                logger.reload("reload :{}", clazz.getName());
            }
        } catch (Exception e) {
            logger.error("refresh mybatis error", e);
        }
    }
}
