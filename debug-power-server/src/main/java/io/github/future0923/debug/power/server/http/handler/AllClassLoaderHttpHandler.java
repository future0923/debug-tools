package io.github.future0923.debug.power.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.power.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.power.server.DebugPowerBootstrap;
import org.codehaus.groovy.reflection.SunClassLoader;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class AllClassLoaderHttpHandler extends BaseHttpHandler<Void, Set<AllClassLoaderRes>> {

    public static final AllClassLoaderHttpHandler INSTANCE = new AllClassLoaderHttpHandler();

    public static final String PATH = "/allClassLoader";

    public static final Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    private AllClassLoaderHttpHandler() {

    }

    @Override
    protected Set<AllClassLoaderRes> doHandle(Void req, Headers responseHeaders) {
        Instrumentation instrumentation = DebugPowerBootstrap.INSTANCE.getInstrumentation();
        Set<AllClassLoaderRes> allClassLoaderResSet = new HashSet<>();
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null
                    // groovy的加载器不要
                    && !(classLoader instanceof SunClassLoader)
                    // DelegatingClassLoader是jdk底层用来提升反射效率的加载器
                    && !classLoader.getClass().getSimpleName().equals("DelegatingClassLoader")) {
                AllClassLoaderRes res = new AllClassLoaderRes(classLoader);
                allClassLoaderResSet.add(res);
                classLoaderMap.put(res.getIdentity(), classLoader);
            }
        }
        return allClassLoaderResSet;
    }
}
