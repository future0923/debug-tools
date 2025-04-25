package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.exception.DefaultClassLoaderException;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import org.codehaus.groovy.reflection.SunClassLoader;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author future0923
 */
public class AllClassLoaderHttpHandler extends BaseHttpHandler<Void, AllClassLoaderRes> {

    public static final AllClassLoaderHttpHandler INSTANCE = new AllClassLoaderHttpHandler();

    public static final String PATH = "/allClassLoader";

    private static final Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    private AllClassLoaderHttpHandler() {

    }

    public static Map<String, ClassLoader> getClassLoaderMap() {
        if (classLoaderMap.isEmpty()) {
            Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                ClassLoader classLoader = clazz.getClassLoader();
                if (classLoader != null
                        // groovy的加载器不要
                        && !(classLoader instanceof SunClassLoader)
                        // DelegatingClassLoader是jdk底层用来提升反射效率的加载器
                        && !classLoader.getClass().getSimpleName().equals("DelegatingClassLoader")) {
                    AllClassLoaderRes.Item item = new AllClassLoaderRes.Item(classLoader);
                    classLoaderMap.put(item.getIdentity(), classLoader);
                }
            }
        }
        return classLoaderMap;
    }

    public static ClassLoader getDebugToolsClassLoader() {
        return AllClassLoaderHttpHandler.class.getClassLoader();
    }

    public static ClassLoader getClassLoader(String identity) throws DefaultClassLoaderException {
        ClassLoader classLoader = getClassLoaderMap().get(identity);
        if (classLoader == null) {
            throw new DefaultClassLoaderException(identity + " ClassLoader Not Found");
        }
        return classLoader;
    }

    @Override
    protected AllClassLoaderRes doHandle(Void req, Headers responseHeaders) {
        final Map<String, ClassLoader> loaderMap = getClassLoaderMap();
        AllClassLoaderRes res = new AllClassLoaderRes();
        res.setItemList(loaderMap.values().stream().map(AllClassLoaderRes.Item::new).collect(Collectors.toSet()));
        for (AllClassLoaderRes.Item item : res.getItemList()) {
            if (item.getName().equals("org.springframework.boot.loader.LaunchedURLClassLoader")) {
                res.setDefaultIdentity(item.getIdentity());
                break;
            }
        }
        if (res.getDefaultIdentity() == null) {
            for (AllClassLoaderRes.Item item : res.getItemList()) {
                if (item.getName().contains("AppClassLoader")) {
                    res.setDefaultIdentity(item.getIdentity());
                    break;
                }
            }
        }
        return res;
    }
}
