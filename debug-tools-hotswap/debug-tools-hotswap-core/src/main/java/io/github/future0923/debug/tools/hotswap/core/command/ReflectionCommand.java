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
package io.github.future0923.debug.tools.hotswap.core.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 在目标类加载器中调用，这个命令通过反射调用
 */
public class ReflectionCommand extends MergeableCommand {

    private static final Logger LOGGER = Logger.getLogger(ReflectionCommand.class);

    /**
     * 可以传入指定目标对象运行方法，也会转换为{@link #className}运行
     */
    private Object target;

    /**
     * 要运行的方法所在的类名
     */
    private final String className;

    /**
     * 要运行的方法名
     */
    @Getter
    private final String methodName;

    /**
     * 运行方法时传入的参数
     */
    @Getter
    private List<Object> params = new ArrayList<>();

    /**
     * 解析目标类的插件对象(可能null)
     */
    private Object plugin;

    /**
     * 执行命令的应用程序类加载器。如果为null，则使用代理类加载器。
     */
    @Setter
    private ClassLoader targetClassLoader;

    /**
     * 注册监听者，当命令运行完成后可以拿到运行结果
     */
    @Getter
    @Setter
    private CommandExecutionListener commandExecutionListener;

    public ReflectionCommand(Object plugin, String className, String methodName, ClassLoader targetClassLoader, Object... params) {
        this.plugin = plugin;
        this.className = className;
        this.methodName = methodName;
        this.targetClassLoader = targetClassLoader;
        this.params = Arrays.asList(params);
    }

    public ReflectionCommand(Object plugin, String className, String methodName) {
        this.plugin = plugin;
        this.className = className;
        this.methodName = methodName;
    }

    public ReflectionCommand(Object target, String methodName, Object... params) {
        this.target = target;
        this.className = target == null ? "NULL" : target.getClass().getName();
        this.methodName = methodName;
        this.params = Arrays.asList(params);
    }


    @Override
    public String toString() {
        return "Command{" +
                "class='" + getClassName() + '\'' +
                ", methodName='" + getMethodName() + '\'' +
                '}';
    }

    public String getClassName() {
        if (className == null && target != null) {
            return target.getClass().getName();
        }
        return className;
    }

    public ClassLoader getTargetClassLoader() {
        if (targetClassLoader == null) {
            if (target != null) {
                return target.getClass().getClassLoader();
            }
            return PluginManager.getInstance().getPluginRegistry().getAppClassLoader(plugin);
        }
        return targetClassLoader;
    }

    @Override
    public void executeCommand() {
        // 是否需要更改类加载器
        if (getTargetClassLoader() != null) {
            Thread.currentThread().setContextClassLoader(getTargetClassLoader());
        }

        ClassLoader targetClassLoader = Thread.currentThread().getContextClassLoader();

        String className = getClassName();
        String method = getMethodName();
        List<Object> params = getParams();

        Object result = null;
        try {
            result = doExecuteReflectionCommand(targetClassLoader, className, target, method, params);
        } catch (ClassNotFoundException e) {
            LOGGER.error("Class {} not found in classloader {}", e, className, targetClassLoader);
        } catch (NoClassDefFoundError e) {
            LOGGER.error("NoClassDefFoundError for class {} in classloader {}", e, className, targetClassLoader);
        } catch (InstantiationException e) {
            LOGGER.error("Unable instantiate class {} in classloader {}", e, className, targetClassLoader);
        } catch (IllegalAccessException e) {
            LOGGER.error("Method {} not public in class {}", e, method, className);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Method {} not found in class {}", e, method, className);
        } catch (InvocationTargetException e) {
            LOGGER.error("Error execute method {} in class {}", e, method, className);
        }

        // 通知监听者
        CommandExecutionListener listener = getCommandExecutionListener();
        if (listener != null) {
            listener.commandExecuted(result);
        }
    }

    /**
     * 反射调用方法
     */
    protected Object doExecuteReflectionCommand(ClassLoader targetClassLoader, String className, Object target, String method, List<Object> params) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> classInAppClassLoader = Class.forName(className, true, targetClassLoader);

        LOGGER.trace("Executing command: requestedClassLoader={}, resolvedClassLoader={}, class={}, method={}, params={}",
                targetClassLoader, classInAppClassLoader.getClassLoader(), classInAppClassLoader, method, params);

        Class<?>[] paramTypes = new Class<?>[params.size()];
        int i = 0;
        for (Object param : params) {
            if (param == null)
                throw new IllegalArgumentException("Cannot execute for null parameter value." + className + method);
            else {
                paramTypes[i++] = param.getClass();
            }
        }

        Method m = classInAppClassLoader.getDeclaredMethod(method, paramTypes);

        return m.invoke(target, params.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReflectionCommand)) return false;

        ReflectionCommand that = (ReflectionCommand) o;

        if (!className.equals(that.className)) return false;
        if (!methodName.equals(that.methodName)) return false;
        if (!params.equals(that.params)) return false;
        if (plugin != null ? !plugin.equals(that.plugin) : that.plugin != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        if (targetClassLoader != null ? !targetClassLoader.equals(that.targetClassLoader) : that.targetClassLoader != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target != null ? target.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (plugin != null ? plugin.hashCode() : 0);
        result = 31 * result + (targetClassLoader != null ? targetClassLoader.hashCode() : 0);
        return result;
    }
}
