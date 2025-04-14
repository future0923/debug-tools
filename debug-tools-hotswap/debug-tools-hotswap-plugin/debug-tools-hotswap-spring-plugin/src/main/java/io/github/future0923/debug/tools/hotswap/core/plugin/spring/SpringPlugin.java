/*
 * Copyright 2013-2024 the HotswapAgent authors.
 *
 * This file is part of HotswapAgent.
 *
 * HotswapAgent is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 2 of the License, or (at your
 * option) any later version.
 *
 * HotswapAgent is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with HotswapAgent. If not, see http://www.gnu.org/licenses/.
 */
package io.github.future0923.debug.tools.hotswap.core.plugin.spring;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.getbean.ProxyReplacerTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerAgent;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.ClassPathBeanDefinitionScannerTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer.SpringBeanClassFileTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer.SpringBeanWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Spring热重载插件
 */
@Plugin(
        name = "Spring",
        description = "Reload Spring configuration after class definition/change.",
        testedVersions = {"All between 3.1.0 - 5.3.30"}, expectedVersions = {"3x", "4x", "5x"},
        supportClass = {
                ClassPathBeanDefinitionScannerTransformer.class,
                ProxyReplacerTransformer.class
        }
)
public class SpringPlugin {

    private static final Logger logger = Logger.getLogger(SpringPlugin.class);

    public static String[] basePackagePrefixes;

    @Init
    HotswapTransformer hotswapTransformer;

    @Init
    Watcher watcher;

    @Init
    Scheduler scheduler;

    @Init
    ClassLoader appClassLoader;

    /**
     * 当DefaultListableBeanFactory加载时初始化SpringPlugin插件并调用freezeConfiguration方法
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.DefaultListableBeanFactory")
    public static void register(CtClass clazz) throws NotFoundException, CannotCompileException {
        StringBuilder src = new StringBuilder("{");
        src.append("setCacheBeanMetadata(false);");
        // 为每个ClassLoader注册Spring Bean插件
        src.append(PluginManagerInvoker.buildInitializePlugin(SpringPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(SpringPlugin.class, "init", "org.springframework.core.SpringVersion.getVersion()", String.class.getName()));
        src.append("}");
        for (CtConstructor constructor : clazz.getDeclaredConstructors()) {
            constructor.insertBeforeBody(src.toString());
        }
        // freezeConfiguration() 的作用是将当前的 Bean 定义注册状态标记为不可修改。执行该方法后，任何对 Bean 定义的添加、删除或修改操作都会抛出异常。这一操作主要用于确保容器的配置在应用运行时保持稳定，从而提高应用的健壮性。
        CtMethod method = clazz.getDeclaredMethod("freezeConfiguration");
        method.insertBefore(
                // 清除SpringBean的name缓存
                "io.github.future0923.debug.tools.hotswap.core.plugin.spring.cache.ResetSpringStaticCaches.resetBeanNamesByType(this); " +
                        // 允许原始 Bean 注入到其他 Bean 中
                        // Spring 容器中的 Bean 通常会被某些机制代理（如 AOP 代理、事务代理等）。在这些情况下，Spring 会用一个代理对象来代替原始 Bean，从而实现额外的功能（例如，方法拦截）。默认情况下，Spring 在注入依赖时会注入代理对象，而不是原始 Bean。
                        //然而，有时我们可能需要绕过代理对象，直接注入原始的 Bean 实例。setAllowRawInjectionDespiteWrapping 就是用来控制是否允许这种行为的。
                        "setAllowRawInjectionDespiteWrapping(true); ");
    }

    /**
     * 初始化Spring插件
     */
    public void init(String version) {
        logger.info("Spring plugin initialized - Spring core version '{}'", getClass().getClassLoader(), version);
        this.initBasePackagePrefixes();
        this.registerBasePackageFromConfiguration();
    }

    /**
     * 初始化包路径前缀
     */
    private void initBasePackagePrefixes() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration(this.appClassLoader);
        if (basePackagePrefixes == null || basePackagePrefixes.length == 0) {
            basePackagePrefixes = pluginConfiguration.getBasePackagePrefixes();
        } else {
            String[] newBasePackagePrefixes = pluginConfiguration.getBasePackagePrefixes();
            List<String> both = new ArrayList<>(basePackagePrefixes.length + newBasePackagePrefixes.length);
            Collections.addAll(both, basePackagePrefixes);
            Collections.addAll(both, newBasePackagePrefixes);
            basePackagePrefixes = both.toArray(new String[0]);
        }
    }

    /**
     * 注册包前缀处理
     */
    public void registerBasePackageFromConfiguration() {
        if (basePackagePrefixes != null) {
            for (String basePackagePrefix : basePackagePrefixes) {
                this.registerBasePackage(basePackagePrefix);
            }
        }
    }

    /**
     * 创建对应包下变动的{@link SpringBeanClassFileTransformer}，可以处理class的redefine事件
     */
    private void registerBasePackage(final String basePackage) {
        hotswapTransformer.registerTransformer(appClassLoader, DebugToolsStringUtils.getClassNameRegExp(basePackage), new SpringBeanClassFileTransformer(appClassLoader, scheduler, basePackage));
    }

    /**
     * 注册热重载 {@link SpringBeanClassFileTransformer}处理class修改，并扫描BasePackage添加 {@link SpringBeanWatchEventListener} 处理新增文件
     * <p>
     * {@link ClassPathBeanDefinitionScannerAgent#registerBasePackage(String)}会反射调用这里注册SpringBasePackage
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
                logger.debug("Spring basePackage '{}' - unable to watch files on URL '{}' for changes (JAR file?), limited hotswap reload support. Use extraClassPath configuration to locate class file on filesystem.", basePackage, basePackageURL);
            } else {
                watcher.addEventListener(appClassLoader, basePackage, basePackageURL, new SpringBeanWatchEventListener(scheduler, appClassLoader, basePackage));
            }
        }
    }

    /**
     * 禁用Cglib缓存
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.aop.framework.CglibAopProxy")
    public static void cglibAopProxyDisableCache(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("createEnhancer");
        method.setBody("{" +
                "org.springframework.cglib.proxy.Enhancer enhancer = new org.springframework.cglib.proxy.Enhancer();" +
                "enhancer.setUseCache(false);" +
                "return enhancer;" +
                "}");

        logger.debug("org.springframework.aop.framework.CglibAopProxy - cglib Enhancer cache disabled");
    }

    /**
     * 当 AbstractApplicationContext refresh方法执行完成后（Spring应用上下文已经初始化完成）初始化
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.context.support.AbstractApplicationContext")
    public static void patchAbstractApplicationContext(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod buildSqlSessionFactory = ctClass.getDeclaredMethod("refresh");
        buildSqlSessionFactory.insertAfter("{" +
                        ClassPathBeanDefinitionScannerAgent.class.getName() + ".initPathBeanNameMapping();" +
                "}");
    }

    /**
     * Controller初始化的时候getCandidateBeanNames获取ioc中所有的beanName，这里需要过滤掉被删除的beanName。<a href="https://github.com/future0923/debug-tools/issues/23">https://github.com/future0923/debug-tools/issues/23</a>
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.web.servlet.handler.AbstractHandlerMethodMapping")
    public static void patchAbstractHandlerMethodMapping(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        CtMethod getCandidateBeanNames = ctClass.getDeclaredMethod("getCandidateBeanNames");
        getCandidateBeanNames.setBody("{" +
                "java.lang.String[] original = (this.detectHandlerMethodsInAncestorContexts ? " +
                "   org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), java.lang.Object.class) :" +
                "   obtainApplicationContext().getBeanNamesForType(java.lang.Object.class));" +
                "return " + ClassPathBeanDefinitionScannerAgent.class.getName() + ".filterDeleteBeanName(original);" +
                "}");
    }
}
