package io.github.future0923.debug.tools.server.compiler;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
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
        options.add("-Xlint:unchecked");
        options.add("-g");
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
                        throw new DynamicCompilerException("Compilation Warnings", warnings);
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
