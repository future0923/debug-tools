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
package io.github.future0923.debug.tools.hotswap.core.plugin.feign;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.annotation.Init;
import io.github.future0923.debug.tools.hotswap.core.annotation.LoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassLoadEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.Plugin;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.command.FeignClientReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.utils.FeignUtils;
import io.github.future0923.debug.tools.hotswap.core.plugin.feign.watcher.FeignClientWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.PluginManagerInvoker;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * Feign 热重载插件
 *
 * @author future0923
 */
@Plugin(
        name = "Feign",
        description = "Reload Feign client after interface definition/change.",
        testedVersions = {"All between 2.0.0 - 4.0.0"},
        expectedVersions = {"2.x", "3.x", "4.x"}
)
public class FeignPlugin {

    private static final Logger logger = Logger.getLogger(FeignPlugin.class);

    @Init
    static Scheduler scheduler;

    @Init
    static Watcher watcher;

    @Init
    static ClassLoader appClassLoader;

    /**
     * 不能使用注解，因为注解只能获取AppClassLoader
     */
    private static ClassLoader userClassLoader;

    /**
     * openfeign的注册者
     */
    private static Object feignClientsRegistrar;

    /**
     * openfeign 重写之后的 scanner
     */
    private static ClassPathScanningCandidateComponentProvider scanner;

    /**
     * spring 的 BeanDefinition 注册表
     */
    private static BeanDefinitionRegistry registry;

    /**
     * 获取OpenFeign的类加载器和注册者
     */
    public void init(ClassLoader classLoader, Object feignClientsRegistrar) {
        FeignPlugin.userClassLoader = classLoader;
        FeignPlugin.feignClientsRegistrar = feignClientsRegistrar;
    }

    @OnClassLoadEvent(classNameRegexp = "org.springframework.cloud.openfeign.FeignClientsRegistrar")
    public static void patchFeignClientsRegistrar(CtClass ctClass, ClassPool classPool) throws NotFoundException, CannotCompileException {
        StringBuilder src = new StringBuilder("{");
        src.append(PluginManagerInvoker.buildInitializePlugin(FeignPlugin.class));
        src.append(PluginManagerInvoker.buildCallPluginMethod(FeignPlugin.class, "init",
                "org.mybatis.spring.SqlSessionFactoryBean.class.getClassLoader()", ClassLoader.class.getName(),
                "this", Object.class.getName()));
        src.append("}");
        CtConstructor[] constructors = ctClass.getConstructors();
        for (CtConstructor constructor : constructors) {
            constructor.insertAfter(src.toString());
        }

        CtMethod getScanner = ctClass.getDeclaredMethod("getScanner");
        getScanner.insertAfter("{" +
                "   io.github.future0923.debug.tools.hotswap.core.plugin.feign.FeignPlugin.registerScanner($_);" +
                "}");

        CtMethod getBasePackages = ctClass.getDeclaredMethod("getBasePackages", new CtClass[]{classPool.get("org.springframework.core.type.AnnotationMetadata")});
        getBasePackages.insertAfter("{" +
                "   io.github.future0923.debug.tools.hotswap.core.plugin.feign.FeignPlugin.registerBasePackage($_);" +
                "}");

        CtMethod registerBeanDefinitions = ctClass.getDeclaredMethod("registerBeanDefinitions", new CtClass[]{classPool.get("org.springframework.core.type.AnnotationMetadata"), classPool.get("org.springframework.beans.factory.support.BeanDefinitionRegistry")});
        registerBeanDefinitions.insertAfter("{" +
                "   io.github.future0923.debug.tools.hotswap.core.plugin.feign.FeignPlugin.registerBeanDefinitionRegistry($2);" +
                "}");
    }

    /**
     * 注册 scanner
     */
    public static void registerScanner(ClassPathScanningCandidateComponentProvider scanner) {
        FeignPlugin.scanner = scanner;
    }

    /**
     * 注册 BeanDefinitionRegistry
     */
    public static void registerBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
        FeignPlugin.registry = registry;
    }

    /**
     * 注册 basePackages
     */
    public static void registerBasePackage(Set<String> basePackages) {
        for (String basePackage : basePackages) {
            registerBasePackage(basePackage);
        }
    }

    public static void registerBasePackage(final String basePackage) {
        String classNameRegExp = DebugToolsStringUtils.getClassNameRegExp(basePackage);
        Enumeration<URL> resourceUrls;
        try {
            resourceUrls = ClassLoaderHelper.getResources(FeignPlugin.getUserClassLoader(), classNameRegExp);
        } catch (IOException e) {
            logger.error("Unable to resolve mapper base package {} in classloader {}.", classNameRegExp, FeignPlugin.getUserClassLoader());
            return;
        }
        while (resourceUrls.hasMoreElements()) {
            URL basePackageURL = resourceUrls.nextElement();
            if (!IOUtils.isFileURL(basePackageURL)) {
                logger.debug("feign basePackage '{}' - unable to watch files on URL '{}' for changes (JAR file?), limited hotswap reload support. Use extraClassPath configuration to locate class file on filesystem.", basePackage, basePackageURL);
            } else {
                watcher.addEventListener(FeignPlugin.getUserClassLoader(), basePackage, basePackageURL, new FeignClientWatchEventListener(scheduler, FeignPlugin.getUserClassLoader(), basePackage));
            }
        }
    }

    /**
     * 重新加载feign client代理类
     */
    @OnClassLoadEvent(classNameRegexp = ".*", events = LoadEvent.REDEFINE)
    public static void redefineFeignClass(final Class<?> clazz, final ClassLoader appClassLoader, final byte[] bytes) {
        if (FeignUtils.isFeignClient(appClassLoader, clazz)) {
            scheduler.scheduleCommand(new FeignClientReloadCommand(appClassLoader, clazz.getName(), bytes, ""), 1000);
        }
    }

    public static ClassLoader getUserClassLoader() {
        return userClassLoader == null ? appClassLoader : userClassLoader;
    }

    public static Object getFeignClientsRegistrar() {
        return feignClientsRegistrar;
    }

    public static ClassPathScanningCandidateComponentProvider getScanner() {
        return scanner;
    }

    public static BeanDefinitionRegistry getRegistry() {
        return registry;
    }

}
