/*
 * Copyright (C) 2024-2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.solon;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.agent.AppContextAgent;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.holder.InjectMappingHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.holder.RoutingTableDefaultHolder;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.transformer.SolonBeanClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.solon.transformer.SolonBeanWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Solon热重载插件
 *
 * @author future0923
 */
@Plugin(
        name = "Solon",
        description = "Reload Solon after class definition/change.",
        testedVersions = {"3.3.1"},
        expectedVersions = {"3.3.1"}
)
public class SolonPlugin {

    private static final Logger logger = Logger.getLogger(SolonPlugin.class);

    @Init
    HotswapTransformer hotswapTransformer;

    @Init
    Watcher watcher;

    @Init
    Scheduler scheduler;

    @Init
    ClassLoader appClassLoader;

    /**
     * 初始化SolonPlugin插件
     */
    @OnClassLoadEvent(classNameRegexp = "org.noear.solon.SolonApp")
    public static void register(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        StringBuilder src = new StringBuilder("{");
        // 为每个ClassLoader注册Solon插件
        src.append(PluginManagerInvoker.buildInitializePlugin(SolonPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(SolonPlugin.class, "init", "org.noear.solon.Solon.version()", String.class.getName()));
        src.append("}");
        for (CtConstructor constructor : ctClass.getDeclaredConstructors()) {
            constructor.insertBeforeBody(src.toString());
        }
    }

    /**
     * 打印Solon信息
     */
    public void init(String version) {
        logger.info("Solon plugin initialized in {} - Solon core version '{}' - {}", getClass().getClassLoader(), version, appClassLoader);
    }

    /**
     * 获取Solon的BasePackage信息
     */
    @OnClassLoadEvent(classNameRegexp = "org.noear.solon.core.AppContext")
    public static void patchAppContext(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod beanScan = ctClass.getDeclaredMethod("beanScan", new CtClass[]{
                classPool.get("java.lang.ClassLoader"),
                classPool.get("java.lang.String")
        });
        beanScan.insertAfter("{" +
                "   " + AppContextAgent.class.getName() + ".getInstance(this).registerBasePackage($2);" +
                "}");
    }

    /**
     * 监听Solon的BasePackage路径下的文件变化
     */
    public void registerComponentScanBasePackage(final String basePackage) {
        logger.info("Registering basePackage {}", basePackage);
        this.registerBasePackage(basePackage);
        Enumeration<URL> resourceUrls;
        try {
            resourceUrls = ClassLoaderHelper.getResources(appClassLoader, basePackage);
        } catch (IOException e) {
            logger.error("Unable to resolve base package {} in classloader {}.", basePackage, appClassLoader);
            return;
        }
        // watch所有应用程序资源的变化。
        while (resourceUrls.hasMoreElements()) {
            URL basePackageURL = resourceUrls.nextElement();
            if (!IOUtils.isFileURL(basePackageURL)) {
                logger.debug("Solon basePackage '{}' - unable to watch files on URL '{}' for changes (JAR file?), limited hotswap reload support. Use extraClassPath configuration to locate class file on filesystem.", basePackage, basePackageURL);
            } else {
                watcher.addEventListener(appClassLoader, basePackage, basePackageURL, new SolonBeanWatchEventListener(scheduler, appClassLoader, basePackage));
            }
        }
    }

    /**
     * 创建对应包下变动的{@link SolonBeanClassFileTransformer}，可以处理class的redefine事件
     */
    private void registerBasePackage(final String basePackage) {
        hotswapTransformer.registerTransformer(appClassLoader, DebugToolsStringUtils.getClassNameRegExp(basePackage), new SolonBeanClassFileTransformer(appClassLoader, scheduler, basePackage));
    }

    /**
     * 构建普通对象注入关系映射
     */
    @OnClassLoadEvent(classNameRegexp = "org.noear.solon.core.wrap.FieldWrap")
    public static void patchFieldWrap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtClass objCtClass = classPool.get("java.lang.Object");
        CtMethod setValue = ctClass.getDeclaredMethod("setValue", new CtClass[]{
                objCtClass,
                objCtClass,
                CtClass.booleanType
        });
        setValue.insertAfter("{" +
                "   " + InjectMappingHolder.class.getName() + ".put($1, $2);" +
                "}");
    }

    /**
     * 构建代理对象注入关系映射
     */
    @OnClassLoadEvent(classNameRegexp = "org.noear.solon.core.BeanWrap")
    public static void patchBeanWrap(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod proxySet = ctClass.getDeclaredMethod("proxySet", new CtClass[]{classPool.get("org.noear.solon.core.BeanWrap$Proxy")});
        proxySet.insertAfter("{" +
                "   if (raw != null && raw != rawUnproxied) {" +
                "   " + InjectMappingHolder.class.getName() + ".putProxy(raw);" +
                "   }" +
                "}");
    }

    /**
     * 收集Solon的路由信息
     */
    @OnClassLoadEvent(classNameRegexp = "org.noear.solon.core.route.RoutingTableDefault")
    public static void patchRoutingTableDefault(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[0]);
        constructor.insertAfter("{" +
                "   " + RoutingTableDefaultHolder.class.getName() + ".add(this);" +
                "}");
    }

}
