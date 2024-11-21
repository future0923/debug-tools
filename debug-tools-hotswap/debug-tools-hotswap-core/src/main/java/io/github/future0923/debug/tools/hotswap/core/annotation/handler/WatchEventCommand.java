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
package io.github.future0923.debug.tools.hotswap.core.annotation.handler;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.annotation.FileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnClassFileEvent;
import io.github.future0923.debug.tools.hotswap.core.annotation.OnResourceFileEvent;
import io.github.future0923.debug.tools.hotswap.core.command.MergeableCommand;
import io.github.future0923.debug.tools.hotswap.core.javassist.ClassPool;
import io.github.future0923.debug.tools.hotswap.core.javassist.CtClass;
import io.github.future0923.debug.tools.hotswap.core.javassist.LoaderClassPath;
import io.github.future0923.debug.tools.hotswap.core.javassist.NotFoundException;
import io.github.future0923.debug.tools.hotswap.core.util.IOUtils;
import io.github.future0923.debug.tools.hotswap.core.watch.WatchFileEvent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 通过WatchEventCommand来调用{@link OnClassFileEvent}和{@link OnResourceFileEvent}的所在类
 */
public class WatchEventCommand<T extends Annotation> extends MergeableCommand {

    private static final Logger LOGGER = Logger.getLogger(WatchEventCommand.class);

    private final PluginAnnotation<T> pluginAnnotation;
    private final WatchEventDTO watchEventDTO;
    private final WatchFileEvent event;
    private final ClassLoader classLoader;

    public static <T extends Annotation> WatchEventCommand<T> createCmdForEvent(PluginAnnotation<T> pluginAnnotation,
            WatchFileEvent event, ClassLoader classLoader) {
        WatchEventDTO watchEventDTO = WatchEventDTO.parse(pluginAnnotation.getAnnotation());
        if (!watchEventDTO.accept(event)) {
            return null;
        }
        if (watchEventDTO.isOnlyRegularFiles() && !event.isFile()) {
            LOGGER.trace("Skipping URI {} because it is not a regular file.", event.getURI());
            return null;
        }
        if (!Arrays.asList(watchEventDTO.getEvents()).contains(event.getEventType())) {
            LOGGER.trace("Skipping URI {} because it is not a requested event.", event.getURI());
            return null;
        }
        if (watchEventDTO.getFilter() != null && !watchEventDTO.getFilter().isEmpty()) {
            if (!event.getURI().toString().matches(watchEventDTO.getFilter())) {
                LOGGER.trace("Skipping URI {} because it does not match filter.", event.getURI(), watchEventDTO.getFilter());
                return null;
            }
        }
        return new WatchEventCommand<>(pluginAnnotation, event, classLoader, watchEventDTO);
    }

    private WatchEventCommand(PluginAnnotation<T> pluginAnnotation, WatchFileEvent event, ClassLoader classLoader, WatchEventDTO watchEventDTO) {
        this.pluginAnnotation = pluginAnnotation;
        this.event = event;
        this.classLoader = classLoader;
        this.watchEventDTO = watchEventDTO;
    }

    @Override
    public void executeCommand() {
        LOGGER.trace("Executing for pluginAnnotation={}, event={} at classloader {}", pluginAnnotation, event, classLoader);
        onWatchEvent(pluginAnnotation, event, classLoader);
    }

    /**
     * 反射调用注解所在的方法
     */
    public void onWatchEvent(PluginAnnotation<T> pluginAnnotation, WatchFileEvent event, ClassLoader classLoader) {
        Object plugin = pluginAnnotation.getPlugin();
        CtClass ctClass = null;
        if (watchEventDTO.isClassFileEvent()) {
            try {
                ctClass = createCtClass(event.getURI(), classLoader);
            } catch (Exception e) {
                LOGGER.error("Unable create CtClass for URI '{}'.", e, event.getURI());
                return;
            }
            if (ctClass == null || !ctClass.getName().matches(watchEventDTO.getClassNameRegexp())) {
                return;
            }
        }
        LOGGER.debug("Executing resource changed method {} on class {} for event {}", pluginAnnotation.getMethod().getName(), plugin.getClass().getName(), event);
        List<Object> args = new ArrayList<>();
        for (Class<?> type : pluginAnnotation.getMethod().getParameterTypes()) {
            if (type.isAssignableFrom(ClassLoader.class)) {
                args.add(classLoader);
            } else if (type.isAssignableFrom(URI.class)) {
                args.add(event.getURI());
            } else if (type.isAssignableFrom(URL.class)) {
                try {
                    args.add(event.getURI().toURL());
                } catch (MalformedURLException e) {
                    LOGGER.error("Unable to convert URI '{}' to URL.", e, event.getURI());
                    return;
                }
            } else if (type.isAssignableFrom(ClassPool.class)) {
                args.add(ClassPool.getDefault());
            } else if (type.isAssignableFrom(FileEvent.class)) {
                args.add(event.getEventType());
            } else if (watchEventDTO.isClassFileEvent() && type.isAssignableFrom(CtClass.class)) {
                args.add(ctClass);
            } else if (watchEventDTO.isClassFileEvent() && type.isAssignableFrom(String.class)) {
                args.add(ctClass != null ? ctClass.getName() : null);
            } else {
                LOGGER.error("Unable to call method {} on plugin {}. Method parameter type {} is not recognized.",
                        pluginAnnotation.getMethod().getName(), plugin.getClass().getName(), type);
                return;
            }
        }
        try {
            pluginAnnotation.getMethod().invoke(plugin, args.toArray());

            // close CtClass if created from here
            if (ctClass != null) {
                ctClass.detach();
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("IllegalAccessException in method '{}' class '{}' classLoader '{}' on plugin '{}'",
                e, pluginAnnotation.getMethod().getName(), ctClass != null ? ctClass.getName() : "",
                classLoader != null ? classLoader.getClass().getName() : "", plugin.getClass().getName());
        } catch (InvocationTargetException e) {
            LOGGER.error("InvocationTargetException in method '{}' class '{}' classLoader '{}' on plugin '{}'",
                e, pluginAnnotation.getMethod().getName(), ctClass != null ? ctClass.getName() : "",
                classLoader != null ? classLoader.getClass().getName() : "", plugin.getClass().getName());
        }
    }


    /**
     * Creats javaassist CtClass for bytecode manipulation. Add default classloader.
     *
     * @param uri         uri
     * @param classLoader loader
     * @return created class
     * @throws NotFoundException
     */
    private CtClass createCtClass(URI uri, ClassLoader classLoader) throws NotFoundException, IOException {
        File file = new File(uri);
        if (file.exists()) {
          ClassPool cp = new ClassPool();
          cp.appendClassPath(new LoaderClassPath(classLoader));
          return cp.makeClass(new ByteArrayInputStream(IOUtils.toByteArray(uri)));
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WatchEventCommand that = (WatchEventCommand) o;

        if (classLoader != null ? !classLoader.equals(that.classLoader) : that.classLoader != null) return false;
        if (event != null ? !event.equals(that.event) : that.event != null) return false;
        if (pluginAnnotation != null ? !pluginAnnotation.equals(that.pluginAnnotation) : that.pluginAnnotation != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = pluginAnnotation != null ? pluginAnnotation.hashCode() : 0;
        result = 31 * result + (event != null ? event.hashCode() : 0);
        result = 31 * result + (classLoader != null ? classLoader.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WatchEventCommand{" +
                "pluginAnnotation=" + pluginAnnotation +
                ", event=" + event +
                ", classLoader=" + classLoader +
                '}';
    }
}
