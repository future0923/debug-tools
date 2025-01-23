package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.spring.resource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;

import java.util.Set;

/**
 * @author future0923
 */
public abstract class AbstractMyBatisResourceReload implements MyBatisResourceReload {

    /**
     * 类型
     */
    protected String type;

    /**
     * 资源信息
     */
    protected String loadedResource;

    /**
     * {@link Configuration}中loadedResources字段所有的资源信息
     */
    protected Set<String> loadedResources;

    /**
     * 配置对象
     */
    protected Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * 移除已载入资源，因为{@link XMLMapperBuilder#parse()}方法会判断是否已载入过，不删除无法重新载入
     */
    public void removeLoadedResource() {
        // loadedResources 引用传递，这里删除， Configuration中的loadedResources也会删除
        loadedResources.remove(loadedResource);
    }
}
