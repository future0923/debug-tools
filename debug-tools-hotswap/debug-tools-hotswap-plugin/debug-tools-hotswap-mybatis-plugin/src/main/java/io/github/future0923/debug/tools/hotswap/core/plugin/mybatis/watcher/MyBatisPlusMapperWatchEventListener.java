package io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.watcher;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.Scheduler;
import io.github.future0923.debug.tools.hotswap.core.plugin.mybatis.command.MyBatisPlusMapperReloadCommand;
import io.github.future0923.debug.tools.hotswap.core.plugin.spring.transformer.SpringBeanWatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.util.classloader.ClassLoaderHelper;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.io.IOException;
import java.util.Objects;

/**
 * @author future0923
 */
public class MyBatisPlusMapperWatchEventListener implements WatchEventListener {

    private static final Logger logger = Logger.getLogger(SpringBeanWatchEventListener.class);

    private final Scheduler scheduler;

    private final ClassLoader appClassLoader;

    private final String basePackage;

    public MyBatisPlusMapperWatchEventListener(Scheduler scheduler, ClassLoader appClassLoader, String basePackage) {
        this.scheduler = scheduler;
        this.appClassLoader = appClassLoader;
        this.basePackage = basePackage;
    }

    @Override
    public void onEvent(WatchFileEvent event) {
        logger.debug("{}, {}", event.getEventType(), event.getURI().toString());
        // 创建了class新文件
        if (FileEvent.CREATE.equals(event.getEventType()) && event.isFile() && event.getURI().toString().endsWith(".class")) {
            // 检查该类尚未被类加载器加载（避免重复重新加载）。
            String className;
            try {
                className = IOUtils.urlToClassName(event.getURI());
            } catch (IOException e) {
                logger.trace("Watch event on resource '{}' skipped, probably Ok because of delete/create event sequence (compilation not finished yet).", e, event.getURI());
                return;
            }
            if (!ClassLoaderHelper.isClassLoaded(appClassLoader, className)) {
                // 只刷新spring中新产生的classes
                Class<?> clazz;
                try {
                    clazz = appClassLoader.loadClass(className);
                } catch (ClassNotFoundException e) {
                    logger.info("", e);
                    return;
                }
                byte[] bytes = IOUtils.toByteArray(event.getURI());
                scheduler.scheduleCommand(new MyBatisPlusMapperReloadCommand(appClassLoader, clazz, bytes), 500);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyBatisPlusMapperWatchEventListener that = (MyBatisPlusMapperWatchEventListener) o;
        return Objects.equals(appClassLoader, that.appClassLoader) && Objects.equals(basePackage, that.basePackage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appClassLoader, basePackage);
    }
}
