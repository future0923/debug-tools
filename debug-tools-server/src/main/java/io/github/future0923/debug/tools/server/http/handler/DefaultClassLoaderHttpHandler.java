package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.classloader.DefaultClassLoader;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;

/**
 * @author future0923
 */
public class DefaultClassLoaderHttpHandler extends BaseHttpHandler<Void, AllClassLoaderRes.Item> {

    public static final DefaultClassLoaderHttpHandler INSTANCE = new DefaultClassLoaderHttpHandler();

    public static final String PATH = "/defaultClassLoader";

    private DefaultClassLoaderHttpHandler() {

    }

    @Override
    protected AllClassLoaderRes.Item doHandle(Void req, Headers responseHeaders) {
        ClassLoader defaultClassLoader = DefaultClassLoader.getDefaultClassLoader();
        if (defaultClassLoader != null) {
            return new AllClassLoaderRes.Item(defaultClassLoader);
        }
        return null;
    }
}
