package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.reload;

/**
 * @author future0923
 */
public abstract class AbstractMyBatisResourceReload<T> implements MyBatisResourceReload {

    @Override
    @SuppressWarnings("unchecked")
    public void reload(Object object) throws Exception {
        doReload((T) object);
    }

    protected abstract void doReload(T object) throws Exception;
}
