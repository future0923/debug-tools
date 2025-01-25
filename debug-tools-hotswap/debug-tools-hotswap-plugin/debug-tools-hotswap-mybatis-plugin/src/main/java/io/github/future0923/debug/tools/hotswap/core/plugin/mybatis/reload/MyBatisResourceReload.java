package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

/**
 * @author future0923
 */
public interface MyBatisResourceReload {

    /**
     * namespace 为 xml 文件中 mapper 的 namespace 属性。在执行 SQL 时，通过 namespace + SQL ID 精确定位某个 SQL 语句。如 namespace:io.github.debug.tools.mapper.UserMapper
     */
    String NAMESPACE = "namespace";

    /**
     * interface 为 mybatis 中的 mapper 接口。Mapper 接口定义了数据访问方法，与 SQL 语句的 id 绑定并生成代理类。如 interface io.github.debug.tools.mapper.UserMapper
     */
    String INTERFACE = "interface";

    /**
     * file 为 文件真实路径。如 file [/Users/debug/tools/test/target/classes/mapper/UserMapper.xml]
     */
    String FILE = "file";

    /**
     * {@code org.apache.ibatis.session.Configuration#loadedResources} 中会存储已经加载的资源，用于避免重复加载。
     * 其内容通常包括 {@link #NAMESPACE}、{@link #INTERFACE} 和 {@link #FILE}。
     */
    String LOADED_RESOURCES_FIELD = "loadedResources";

    /**
     * 重载 MyBatis 资源
     */
    void reload(Object object) throws Exception;

}
