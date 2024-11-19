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
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.javassist.CannotCompileException;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtConstructor;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtMethod;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.core.BeanDefinitionProcessor;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.ClassChangedCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.PropertiesChangedCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringReloadConfig;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.XmlsChangedCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.YamlChangedCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.scanner.SpringBeanWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.BeanFactoryTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.ClassPathBeanDefinitionScannerTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.ConfigurationClassPostProcessorTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.InitDestroyAnnotationBeanPostProcessorTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.PlaceholderConfigurerSupportTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.PostProcessorRegistrationDelegateTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.ProxyReplacerTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.ResourcePropertySourceTransformer;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformers.XmlBeanDefinitionScannerTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.HotswapTransformer;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.ReflectionHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Spring plugin.
 */
@Plugin(name = "Spring", description = "Reload Spring configuration after class definition/change.",
        testedVersions = {"All between 3.1.0 - 5.3.30"}, expectedVersions = {"3x", "4x", "5x"},
        supportClass = {ClassPathBeanDefinitionScannerTransformer.class,
                ProxyReplacerTransformer.class,
                ConfigurationClassPostProcessorTransformer.class,
                ResourcePropertySourceTransformer.class,
                PlaceholderConfigurerSupportTransformer.class,
                XmlBeanDefinitionScannerTransformer.class,
                PostProcessorRegistrationDelegateTransformer.class,
                BeanFactoryTransformer.class,
                InitDestroyAnnotationBeanPostProcessorTransformer.class})
public class SpringPlugin {

    private static final Logger LOGGER = Logger.getLogger(SpringPlugin.class);

    public static String[] basePackagePrefixes;

    @Init
    HotswapTransformer hotswapTransformer;

    @Init
    Watcher watcher;

    @Init
    Scheduler scheduler;

    @Init
    ClassLoader appClassLoader;

    private Class<?> springChangeHubClass;

    public void init() throws ClassNotFoundException {
        LOGGER.info("Spring plugin initialized");
        springChangeHubClass = Class.forName("io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedAgent", true, appClassLoader);
        ReflectionHelper.set(null, springChangeHubClass, "appClassLoader", appClassLoader);
        this.registerBasePackageFromConfiguration();
        this.initBasePackagePrefixes();
    }

    public void init(String version) throws ClassNotFoundException {
        LOGGER.info("Spring plugin initialized - Spring core version '{}'", version);
        springChangeHubClass = Class.forName("io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedAgent", true, appClassLoader);
        ReflectionHelper.set(null, springChangeHubClass, "appClassLoader", appClassLoader);
        this.registerBasePackageFromConfiguration();
        this.initBasePackagePrefixes();
    }

    private void initBasePackagePrefixes() {
        PluginConfiguration pluginConfiguration = new PluginConfiguration(this.appClassLoader);
        if (basePackagePrefixes == null || basePackagePrefixes.length == 0) {
            basePackagePrefixes = pluginConfiguration.getBasePackagePrefixes();
            // TODO test
            List<String> both = new ArrayList<>();
            both.add("com.estate.biz.business");
            basePackagePrefixes = both.toArray(new String[0]);
        } else {
            String[] newBasePackagePrefixes = pluginConfiguration.getBasePackagePrefixes();
            List<String> both = new ArrayList<>(basePackagePrefixes.length + newBasePackagePrefixes.length);
            Collections.addAll(both, basePackagePrefixes);
            Collections.addAll(both, newBasePackagePrefixes);
            basePackagePrefixes = both.toArray(new String[0]);
        }
    }

    @OnResourceFileEvent(path = "/", filter = ".*.xml", events = {FileEvent.MODIFY})
    public void registerResourceListeners(URL url) {
        scheduler.scheduleCommand(new XmlsChangedCommand(appClassLoader, url, scheduler));
        LOGGER.trace("Scheduling Spring reload for XML '{}'", url);
        scheduler.scheduleCommand(new SpringChangedReloadCommand(appClassLoader), SpringReloadConfig.reloadDelayMillis);
    }

    @OnResourceFileEvent(path = "/", filter = ".*.properties", events = {FileEvent.MODIFY})
    public void registerPropertiesListeners(URL url) {
        scheduler.scheduleCommand(new PropertiesChangedCommand(appClassLoader, url, scheduler));
        LOGGER.trace("Scheduling Spring reload for properties '{}'", url);
        scheduler.scheduleCommand(new SpringChangedReloadCommand(appClassLoader), SpringReloadConfig.reloadDelayMillis);
    }

    @OnResourceFileEvent(path = "/", filter = ".*.yaml", events = {FileEvent.MODIFY})
    public void registerYamlListeners(URL url) {
        scheduler.scheduleCommand(new YamlChangedCommand(appClassLoader, url, scheduler));
        // schedule reload after 1000 milliseconds
        LOGGER.trace("Scheduling Spring reload for yaml '{}'", url);
        scheduler.scheduleCommand(new SpringChangedReloadCommand(appClassLoader), SpringReloadConfig.reloadDelayMillis);
    }

    @OnClassLoadEvent(classNameRegexp = ".*", events = {LoadEvent.REDEFINE})
    public void registerClassListeners(Class<?> clazz) {
        scheduler.scheduleCommand(new ClassChangedCommand(appClassLoader, clazz, scheduler));
        LOGGER.trace("Scheduling Spring reload for class '{}' in classLoader {}", clazz, appClassLoader);
        scheduler.scheduleCommand(new SpringChangedReloadCommand(appClassLoader), SpringReloadConfig.reloadDelayMillis);
    }

    /**
     * register base package prefix from configuration file
     */
    public void registerBasePackageFromConfiguration() {
        if (basePackagePrefixes != null) {
            for (String basePackagePrefix : basePackagePrefixes) {
                this.registerBasePackage(basePackagePrefix);
            }
        }
    }

    private void registerBasePackage(final String basePackage) {
        // Force load/initialize the ClassPathBeanRefreshCommand class into the JVM to work around an issue where instances
        // of this class sometimes remain locked during the agent's transform() call. This behavior suggests a potential
        // bug in JVMTI or its handling of debugger locks.
        hotswapTransformer.registerTransformer(appClassLoader, getClassNameRegExp(basePackage),
                new SpringBeanClassFileTransformer(appClassLoader, scheduler, basePackage));
    }

    /**
     * Register both hotswap transformer AND watcher - in case of new file the file is not known
     * to JVM and hence no hotswap is called. The file may even exist, but until is loaded by Spring
     * it will not be known by the JVM. File events are processed only if the class is not known to the
     * classloader yet.
     *
     * @param basePackage only files in a basePackage
     */
    public void registerComponentScanBasePackage(final String basePackage) {
        LOGGER.info("Registering basePackage {}", basePackage);

        this.registerBasePackage(basePackage);

        Enumeration<URL> resourceUrls = null;
        try {
            resourceUrls = getResources(basePackage);
        } catch (IOException e) {
            LOGGER.error("Unable to resolve base package {} in classloader {}.", basePackage, appClassLoader);
            return;
        }

        // for all application resources watch for changes
        while (resourceUrls.hasMoreElements()) {
            URL basePackageURL = resourceUrls.nextElement();

            if (!IOUtils.isFileURL(basePackageURL)) {
                LOGGER.debug("Spring basePackage '{}' - unable to watch files on URL '{}' for changes (JAR file?), limited hotswap reload support. " +
                        "Use extraClassPath configuration to locate class file on filesystem.", basePackage, basePackageURL);
            } else {
                watcher.addEventListener(appClassLoader, basePackageURL,
                        new SpringBeanWatchEventListener(scheduler, appClassLoader, basePackage));
            }
        }
    }

    private String getClassNameRegExp(String basePackage) {
        String regexp = basePackage;
        while (regexp.contains("**")) {
            regexp = regexp.replace("**", ".*");
        }
        if (!regexp.endsWith(".*")) {
            regexp += ".*";
        }
        return regexp;
    }

    private Enumeration<URL> getResources(String basePackage) throws IOException {
        String resourceName = basePackage;
        int index = resourceName.indexOf('*');
        if (index != -1) {
            resourceName = resourceName.substring(0, index);
            index = resourceName.lastIndexOf('.');
            if (index != -1) {
                resourceName = resourceName.substring(0, index);
            }
        }
        resourceName = resourceName.replace('.', '/');
        return appClassLoader.getResources(resourceName);
    }

    /**
     * Plugin initialization is after Spring has finished its startup and freezeConfiguration is called.
     * <p>
     * This will override freeze method to init plugin - plugin will be initialized and the configuration
     * remains unfrozen, so bean (re)definition may be done by the plugin.
     */
    @OnClassLoadEvent(classNameRegexp = "org.springframework.beans.factory.support.DefaultListableBeanFactory")
    public static void register(ClassLoader appClassLoader, CtClass clazz, ClassPool classPool) throws NotFoundException, CannotCompileException {
        StringBuilder src = new StringBuilder("{");
        src.append("setCacheBeanMetadata(false);");
        // init a spring plugin with every appclassloader
        src.append(PluginManagerInvoker.buildInitializePlugin(SpringPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(SpringPlugin.class, "init",
                "org.springframework.core.SpringVersion.getVersion()", String.class.getName()));
        src.append("}");

        for (CtConstructor constructor : clazz.getDeclaredConstructors()) {
            constructor.insertBeforeBody(src.toString());
            constructor.insertAfter("io.github.future0923.debug.tools.hotswap.core.plugin.spring.reload.SpringChangedAgent.getInstance(this);");
        }

        // freezeConfiguration cannot be disabled because of performance degradation
        // instead call freezeConfiguration after each bean (re)definition and clear all caches.

        // WARNING - allowRawInjectionDespiteWrapping is not safe, however without this
        //   spring was not able to resolve circular references correctly.
        //   However, the code in AbstractAutowireCapableBeanFactory.doCreateBean() in debugger always
        //   showed that exposedObject == earlySingletonReference and hence everything is Ok.
        // 				if (exposedObject == bean) {
        //                  exposedObject = earlySingletonReference;
        //   The waring is because I am not sure what is going on here.

        CtMethod method = clazz.getDeclaredMethod("freezeConfiguration");
        method.insertBefore(
                "io.github.future0923.debug.tools.hotswap.core.plugin.spring.core.ResetSpringStaticCaches.resetBeanNamesByType(this); " +
                        "setAllowRawInjectionDespiteWrapping(true); ");

        // Patch registerBeanDefinition so that XmlBeanDefinitionScannerAgent has chance to keep track of all beans
        // defined from the XML configuration.
        CtMethod registerBeanDefinitionMethod = clazz.getDeclaredMethod("registerBeanDefinition");
        registerBeanDefinitionMethod.insertBefore(BeanDefinitionProcessor.class.getName() + ".registerBeanDefinition(this, $1, $2);");

        CtMethod removeBeanDefinitionMethod = clazz.getDeclaredMethod("removeBeanDefinition");
        removeBeanDefinitionMethod.insertBefore(BeanDefinitionProcessor.class.getName() + ".removeBeanDefinition(this, $1);");
    }

    @OnClassLoadEvent(classNameRegexp = "org.springframework.aop.framework.CglibAopProxy")
    public static void cglibAopProxyDisableCache(CtClass ctClass) throws NotFoundException, CannotCompileException {
        CtMethod method = ctClass.getDeclaredMethod("createEnhancer");
        method.setBody("{" +
                "org.springframework.cglib.proxy.Enhancer enhancer = new org.springframework.cglib.proxy.Enhancer();" +
                "enhancer.setUseCache(false);" +
                "return enhancer;" +
                "}");

        LOGGER.debug("org.springframework.aop.framework.CglibAopProxy - cglib Enhancer cache disabled");
    }
}
