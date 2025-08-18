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
package io.github.future0923.debug.tools.server.compiler;

import io.github.future0923.debug.tools.base.constants.ProjectConstants;
import io.github.future0923.debug.tools.base.logging.Logger;
import io.github.future0923.debug.tools.base.utils.DebugToolsStringUtils;
import io.github.future0923.debug.tools.hotswap.core.config.PluginConfiguration;
import io.github.future0923.debug.tools.hotswap.core.config.PluginManager;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 动态编译java代码
 *
 * @author future0923
 */
public class DynamicCompiler {

    private static final Logger logger = Logger.getLogger(DynamicCompiler.class);

    /**
     * 编译器
     */
    private final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();

    /**
     * 动态编译时java源文件类文件等管理器
     */
    private final StandardJavaFileManager standardFileManager;

    /**
     * 编译参数
     */
    private final List<String> options = new ArrayList<>();

    /**
     * 动态编译类加载器，用于加载编译类时所需要相关的类
     */
    private final DynamicClassLoader dynamicClassLoader;

    /**
     * 要进行动态编译的类
     */
    private final Collection<JavaFileObject> compilationUnits = new ArrayList<>();

    /**
     * 警告信息
     */
    private final List<Diagnostic<? extends JavaFileObject>> warnings = new ArrayList<>();

    /**
     * 错误信息
     */
    private final List<Diagnostic<? extends JavaFileObject>> errors = new ArrayList<>();

    public DynamicCompiler(ClassLoader classLoader) {
        if (javaCompiler == null) {
            throw new IllegalStateException(
                    "Can not load JavaCompiler from javax.tools.ToolProvider#getSystemJavaCompiler(),"
                            + " please confirm the application running in JDK not JRE.");
        }
        standardFileManager = javaCompiler.getStandardFileManager(null, null, null);
        // 生成调试信息。
        // 告诉编译器为生成的 .class 文件包含调试信息（如变量名、行号等）。
        // 让你在调试或热重载时可以看到源码级别调试（断点、变量）
        options.add("-g");
        if (ProjectConstants.DEBUG) {
            // 打印 annotation processor 的运行轮次（调试用）
            // 告诉 javac 在控制台输出 annotation processing 的执行“轮次”
            options.add("-XprintRounds");
            // 打印被加载的 annotation processor（处理器信息）
            // 告诉 javac 在处理注解时输出加载了哪些注解处理器类
            options.add("-XprintProcessorInfo");
            // 显示泛型相关的未检查警告
            options.add("-Xlint:unchecked");
        }
        // 隐藏 annotation processor 在未来可能默认关闭的警告
        //options.add("-Xlint:-options");
        // 显式开启 annotation processor(8不支持)
        // options.add("-proc:full");
        PluginConfiguration pluginConfiguration = PluginManager.getInstance().getPluginConfiguration(classLoader);
        if (pluginConfiguration != null && DebugToolsStringUtils.isNotBlank(pluginConfiguration.getLombokJarPath())) {
            options.add("-classpath");
            options.add(System.getProperty("java.class.path") + File.pathSeparator + pluginConfiguration.getLombokJarPath());
        }
        dynamicClassLoader = new DynamicClassLoader(classLoader);
    }

    /**
     * 添加要编译的资源，用{@link StringSource}包装
     *
     * @param className 类名
     * @param source    源代码
     */
    public void addSource(String className, String source) {
        addSource(new StringSource(className, source));
    }

    /**
     * 添加要编译的资源，用{@link StringSource}包装
     *
     * @param javaFileObject 源文件对象
     */
    public void addSource(JavaFileObject javaFileObject) {
        compilationUnits.add(javaFileObject);
    }

    /**
     * 编译并返回编译后的类信息
     */
    public Map<String, Class<?>> buildClass() throws ClassNotFoundException {
        build();
        return dynamicClassLoader.getClasses();
    }

    /**
     * 编译并返回编译后的字节码
     */
    public Map<String, byte[]> buildByteCodes() {
        build();
        return dynamicClassLoader.getByteCodes();
    }

    /**
     * 编译
     */
    public void build() {
        errors.clear();
        warnings.clear();
        JavaFileManager fileManager = new DynamicJavaFileManager(standardFileManager, dynamicClassLoader);
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, collector, options, null, compilationUnits);
        try {
            if (!compilationUnits.isEmpty()) {
                boolean result = task.call();
                if (!result || !collector.getDiagnostics().isEmpty()) {
                    for (Diagnostic<? extends JavaFileObject> diagnostic : collector.getDiagnostics()) {
                        switch (diagnostic.getKind()) {
                            case NOTE:
                            case MANDATORY_WARNING:
                            case WARNING:
                                warnings.add(diagnostic);
                                break;
                            case OTHER:
                            case ERROR:
                            default:
                                errors.add(diagnostic);
                                break;
                        }
                    }
                    if (!warnings.isEmpty()) {
                        DynamicCompilerException compilationWarnings = new DynamicCompilerException("Compilation Warnings", warnings);
                        logger.warning(compilationWarnings.getMessage());
                    }
                    if (!errors.isEmpty()) {
                        throw new DynamicCompilerException("Compilation Error", errors);
                    }
                }
            }
        } catch (Throwable e) {
            throw new DynamicCompilerException(e, errors);
        } finally {
            compilationUnits.clear();
        }
    }

    /**
     * 将 Diagnostic 信息转为 string
     */
    private List<String> diagnosticToString(List<Diagnostic<? extends JavaFileObject>> diagnostics) {
        List<String> diagnosticMessages = new ArrayList<String>();
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics) {
            diagnosticMessages.add("line: " + diagnostic.getLineNumber() + ", message: " + diagnostic.getMessage(Locale.US));
        }
        return diagnosticMessages;
    }

    /**
     * 获取异常信息
     */
    public List<String> getErrors() {
        return diagnosticToString(errors);
    }

    /**
     * 获取警告信息
     */
    public List<String> getWarnings() {
        return diagnosticToString(warnings);
    }

    /**
     * 返回动态编译的类加载器
     */
    public DynamicClassLoader getClassLoader() {
        return dynamicClassLoader;
    }
}
