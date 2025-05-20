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
package io.github.future0923.debug.tools.hotswap.core.watch.nio;

import io.github.future0923.debug.tools.base.hutool.core.io.FileUtil;
import com.sun.nio.file.ExtendedWatchEventModifier;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchEventListener;
import io.github.future0923.debug.tools.hotswap.core.watch.Watcher;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * 支持{@link ExtendedWatchEventModifier#FILE_TREE}系统的NIO2观察者实现
 * <a href="http://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java">示例</a>
 */
public abstract class AbstractNIO2Watcher implements Watcher {

    protected Logger LOGGER = Logger.getLogger(this.getClass());

    protected final static WatchEvent.Kind<?>[] KINDS = new WatchEvent.Kind<?>[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY };

    /**
     * 文件观察服务
     */
    protected WatchService watcher;

    /**
     * WatchKey对应的Path
     */
    protected final Map<WatchKey, Path> keys;

    /**
     * PATH变动的监听者
     */
    private final Map<Path, List<WatchEventListener>> listeners = new ConcurrentHashMap<>();

    /**
     * Watch事件对应的类加载器
     */
    protected Map<WatchEventListener, ClassLoader> classLoaderListeners = new ConcurrentHashMap<>();

    /**
     * Watch线程
     */
    private Thread runner;

    /**
     * 是否停止
     */
    private volatile boolean stopped;

    /**
     * 事件分发器
     */
    protected final EventDispatcher dispatcher;

    public AbstractNIO2Watcher() throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new ConcurrentHashMap<>();
        dispatcher = new EventDispatcher(listeners);
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    @Override
    public synchronized void addEventListener(ClassLoader classLoader, URI pathPrefix, WatchEventListener listener) {
        File path;
        try {
            path = new File(pathPrefix);
         } catch (IllegalArgumentException e) {
            if (!LOGGER.isLevelEnabled(Logger.Level.TRACE)) {
                LOGGER.warning("Unable to watch for path {}, not a local regular file or directory.", pathPrefix);
            } else {
                LOGGER.trace("Unable to watch for path {} exception", e, pathPrefix);
            }
            return;
        }

        try {
            addDirectory(path.toPath());
        } catch (IOException e) {
            if (!LOGGER.isLevelEnabled(Logger.Level.TRACE)) {
                LOGGER.warning("Unable to watch for path {}, not a local regular file or directory.", pathPrefix);
            } else {
                LOGGER.trace("Unable to watch path with prefix '{}' for changes.", e, pathPrefix);
            }
            return;
        }

        Path key = Paths.get(pathPrefix);
        List<WatchEventListener> list = listeners.computeIfAbsent(key, k -> new ArrayList<>());

        if (!list.contains(listener)) {
            list.add(listener);
        }

        if (classLoader != null) {
            classLoaderListeners.put(listener, classLoader);
        }
    }

    @Override
    public void addEventListener(ClassLoader classLoader, String basePackage, URL pathPrefix, WatchEventListener listener) {
        if (pathPrefix == null) {
            return;
        }
        try {
            addEventListener(classLoader, pathPrefix.toURI(), listener);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Unable to convert URL to URI " + pathPrefix, e);
        }
        PluginConfiguration configuration = PluginManager.getInstance().getPluginConfiguration(classLoader);
        URL[] extraClasspath = configuration.getExtraClasspath();
        basePackage = DebugToolsStringUtils.getClassNameRemoveStar(basePackage);
        for (URL url : extraClasspath) {
            String basePackagePath = url.getPath() + basePackage.replace(".", File.separator);
            FileUtil.mkdir(basePackagePath);
            // 不能清空文件夹，如果第一个 basePackage 为 /var/tmp/debug/tools，第二个 basePackage 为 /var/tmp 就会把tools文件夹清除，这样第一个watch就失效了
            //FileUtil.clean(basePackagePath);
            addEventListener(classLoader, new File(basePackagePath).toURI(), listener);
        }
    }

    /**
     * 移除所有注册在classLoader中的
     */
    @Override
    public void closeClassLoader(ClassLoader classLoader) {
        for (Iterator<Entry<WatchEventListener, ClassLoader>> entryIterator = classLoaderListeners.entrySet().iterator(); entryIterator.hasNext();) {
            Entry<WatchEventListener, ClassLoader> entry = entryIterator.next();
            if (entry.getValue().equals(classLoader)) {
                entryIterator.remove();
                try {
                    for (Iterator<Entry<Path, List<WatchEventListener>>> listenersIterator = listeners.entrySet().iterator(); listenersIterator.hasNext();) {
                        Entry<Path, List<WatchEventListener>> pathListenerEntry = listenersIterator.next();
                        List<WatchEventListener> l = pathListenerEntry.getValue();

                        if (l != null) {
                            l.remove(entry.getKey());
                        }

                        if (l == null || l.isEmpty()) {
                            listenersIterator.remove();
                        }

                    }
                } catch (Exception e) {
                    LOGGER.error("Ooops", e);
                }
            }
        }
        // cleanup...
        if (classLoaderListeners.isEmpty()) {
            listeners.clear();
            for (WatchKey wk : keys.keySet()) {
                try {
                    wk.cancel();
                } catch (Exception e) {
                    LOGGER.error("Ooops", e);
                }
            }
            try {
                this.watcher.close();
            } catch (IOException e) {
                LOGGER.error("Ooops", e);
            }
            LOGGER.info("All classloaders closed, released watch service..");
            try {
                // Reset
                this.watcher = FileSystems.getDefault().newWatchService();
            } catch (IOException e) {
                LOGGER.error("Ooops", e);
            }
        }
        LOGGER.debug("All watch listeners removed for classLoader {}", classLoader);
    }

    /**
     * 注册Watch目录
     */
    public void addDirectory(Path path) throws IOException {
       registerAll(path, false);
    }

    /**
     * 使用 WatchService 注册给定目录。子目录将自动受到监视（支持文件系统）
     *
     * @param dir 目录
     * @param fromCreateEvent 如果创建了目录，则注册监听下面的child {@code AbstractNIO2Watcher#processEvents()}
     */
    protected abstract void registerAll(final Path dir, boolean fromCreateEvent) throws IOException;

    /**
     * 处理队列中的所有事件
     *
     * @return 是否处理成功
     */
    private boolean processEvents() throws InterruptedException {
        WatchKey key = watcher.poll(10, TimeUnit.MILLISECONDS);
        if (key == null) {
            return true;
        }
        Path dir = keys.get(key);
        if (dir == null) {
            LOGGER.warning("WatchKey '{}' not recognized", key);
            return true;
        }
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();
            if (kind == OVERFLOW) {
                LOGGER.warning("WatchKey '{}' overflowed", key);
                continue;
            }
            WatchEvent<Path> ev = cast(event);
            Path name = ev.context();
            Path child = dir.resolve(name);

            LOGGER.debug("Watch event '{}' on '{}' --> {}", event.kind().name(), child, name);

            dispatcher.add(ev, child);

            // 如果创建了目录，则注册监听下面的child
            if (kind == ENTRY_CREATE) {
                try {
                    if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                        registerAll(child, true);
                    }
                } catch (IOException x) {
                    LOGGER.warning("Unable to register events for directory {}", x, child);
                }
            }
        }

        // 如果目录不再可访问，则重置WatchKey并从集合中删除
        boolean valid = key.reset();
        if (!valid) {
            LOGGER.debug("Watcher on {} not valid, removing path=", keys.get(key));
            keys.remove(key);
            // 所有目录都无法访问
            if (keys.isEmpty()) {
                return false;
            }
            // 取消所有WatchKey
            if (classLoaderListeners.isEmpty()) {
                for (WatchKey k : keys.keySet()) {
                    k.cancel();
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void run() {
        runner = new Thread(() -> {
            try {
                for (;;) {
                    if (stopped || !processEvents()) {
                        break;
                    }
                }
            } catch (InterruptedException ignored) {

            }
        });
        runner.setDaemon(true);
        runner.setName("HotSwap Watcher");
        runner.start();

        dispatcher.start();
    }

    @Override
    public void stop() {
        stopped = true;
    }

    /**
     * 获取Watch事件修饰符
     */
    static WatchEvent.Modifier getWatchEventModifier(String claz, String field) {
        try {
            Class<?> c = Class.forName(claz);
            Field f = c.getField(field);
            return (WatchEvent.Modifier) f.get(c);
        } catch (Exception e) {
            return null;
        }
    }
}