package io.github.future0923.debug.tools.server.http.handler;

import com.sun.net.httpserver.Headers;
import io.github.future0923.debug.tools.base.SpyAPI;
import io.github.future0923.debug.tools.common.protocal.http.AllClassLoaderRes;
import io.github.future0923.debug.tools.common.utils.DebugToolsJvmUtils;
import io.github.future0923.debug.tools.server.DebugToolsBootstrap;
import org.codehaus.groovy.reflection.SunClassLoader;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author future0923
 */
public class AllClassLoaderHttpHandler extends BaseHttpHandler<Void, AllClassLoaderRes> {

    public static final AllClassLoaderHttpHandler INSTANCE = new AllClassLoaderHttpHandler();

    public static final String PATH = "/allClassLoader";

    private static volatile ClassLoader defaultClassLoader;

    public static final Map<String, ClassLoader> classLoaderMap = new ConcurrentHashMap<>();

    public static final List<String> baseClassList = new ArrayList<>();

    static {
        baseClassList.add("org.slf4j.Logger");
        baseClassList.add("org.apache.log4j.Logger");
        baseClassList.add("org.springframework.beans.factory.BeanFactory");
    }

    private AllClassLoaderHttpHandler() {

    }

    public static ClassLoader getDefaultClassLoader() {
        if (defaultClassLoader == null) {
            synchronized (AllClassLoaderHttpHandler.class) {
                if (defaultClassLoader == null) {
                    String mainClass = DebugToolsJvmUtils.getMainClass();
                    Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
                    for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
                        ClassLoader classLoader = clazz.getClassLoader();
                        if (defaultClassLoader == null) {
                            if (mainClass != null) {
                                if (mainClass.equals(clazz.getName())) {
                                    defaultClassLoader = classLoader;
                                    break;
                                }
                            } else if (baseClassList.contains(clazz.getName())) {
                                defaultClassLoader = classLoader;
                                break;
                            }
                        }
                    }
                    if (defaultClassLoader == null) {
                        defaultClassLoader = SpyAPI.class.getClassLoader();
                    }
                }
            }
        }
        return defaultClassLoader;
    }

    public static ClassLoader getDebugToolsClassLoader() {
        return AllClassLoaderHttpHandler.class.getClassLoader();
    }

    @Override
    protected AllClassLoaderRes doHandle(Void req, Headers responseHeaders) {
        AllClassLoaderRes res = new AllClassLoaderRes();
        String mainClass = DebugToolsJvmUtils.getMainClass();
        Instrumentation instrumentation = DebugToolsBootstrap.INSTANCE.getInstrumentation();
        Set<AllClassLoaderRes.Item> allClassLoaderResSet = new HashSet<>();
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (res.getDefaultIdentity() == null) {
                if (mainClass != null) {
                    if (mainClass.equals(clazz.getName())) {
                        res.setDefaultIdentity(Integer.toHexString(System.identityHashCode(classLoader)));
                        defaultClassLoader = classLoader;
                    }
                } else if (baseClassList.contains(clazz.getName())) {
                    res.setDefaultIdentity(Integer.toHexString(System.identityHashCode(classLoader)));
                }

            }
            if (classLoader != null
                    // groovy的加载器不要
                    && !(classLoader instanceof SunClassLoader)
                    // DelegatingClassLoader是jdk底层用来提升反射效率的加载器
                    && !classLoader.getClass().getSimpleName().equals("DelegatingClassLoader")) {
                AllClassLoaderRes.Item item = new AllClassLoaderRes.Item(classLoader);
                allClassLoaderResSet.add(item);
                classLoaderMap.put(item.getIdentity(), classLoader);
            }
        }
        if (res.getDefaultIdentity() == null) {
            res.getItemList().stream().filter(c -> c.getName().startsWith("sun.misc.Launcher$AppClassLoader")).map(AllClassLoaderRes.Item::getIdentity).findFirst().ifPresent(res::setDefaultIdentity);
        }
        res.setItemList(allClassLoaderResSet);
        return res;
    }
}
