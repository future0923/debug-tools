/*
 * Copyright 2024-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.future0923.debug.tools.hotswap.core.command;

import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
     * 运行方法的参数类型
     */
    private final Class<?>[] paramTypes;

    /**
     * 运行方法时传入的参数
     */
    @Getter
    private final List<Object> params;

    /**
     * 解析目标类的插件对象(可能null)
     */
    private final Object plugin;

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

    public ReflectionCommand(ClassLoader targetClassLoader,
                             Object plugin,
                             String className,
                             String methodName,
                             List<Class<?>> paramTypes,
                             Object... params) {
        this.plugin = plugin;
        this.className = className;
        this.methodName = methodName;
        this.targetClassLoader = targetClassLoader;
        this.paramTypes = paramTypes.toArray(new Class[0]);
        this.params = Arrays.asList(params);
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
        if (targetClassLoader != null ? !targetClassLoader.equals(that.targetClassLoader) : that.targetClassLoader != null)
            return false;

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

    @Override
    public String toString() {
        return "ReflectionCommand{" +
                "target=" + target +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", params=" + params +
                ", plugin=" + plugin +
                ", targetClassLoader=" + targetClassLoader +
                '}';
    }
}
