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
package io.github.future0923.debug.tools.base.hutool.core.compiler;


import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * 源码编译工具类，主要封装{@link JavaCompiler} 相关功能
 *
 * @author looly
 * @since 5.5.2
 */
public class CompilerUtil {

	/**
	 * java 编译器
	 */
	public static final JavaCompiler SYSTEM_COMPILER = ToolProvider.getSystemJavaCompiler();

	/**
	 * 编译指定的源码文件
	 *
	 * @param sourceFiles 源码文件路径
	 * @return 0表示成功，否则其他
	 */
	public static boolean compile(String... sourceFiles) {
		return 0 == SYSTEM_COMPILER.run(null, null, null, sourceFiles);
	}

	/**
	 * 获取{@link StandardJavaFileManager}
	 *
	 * @return {@link StandardJavaFileManager}
	 */
	public static StandardJavaFileManager getFileManager() {
		return getFileManager(null);
	}

	/**
	 * 获取{@link StandardJavaFileManager}
	 *
	 * @param diagnosticListener 异常收集器
	 * @return {@link StandardJavaFileManager}
	 * @since 5.5.8
	 */
	public static StandardJavaFileManager getFileManager(DiagnosticListener<? super JavaFileObject> diagnosticListener) {
		return SYSTEM_COMPILER.getStandardFileManager(diagnosticListener, null, null);
	}

	/**
	 * 新建编译任务
	 *
	 * @param fileManager        {@link JavaFileManager}，用于管理已经编译好的文件
	 * @param diagnosticListener 诊断监听
	 * @param options            选项，例如 -cpXXX等
	 * @param compilationUnits   编译单元，即需要编译的对象
	 * @return {@link JavaCompiler.CompilationTask}
	 */
	public static JavaCompiler.CompilationTask getTask(
			JavaFileManager fileManager,
			DiagnosticListener<? super JavaFileObject> diagnosticListener,
			Iterable<String> options,
			Iterable<? extends JavaFileObject> compilationUnits) {
		return SYSTEM_COMPILER.getTask(null, fileManager, diagnosticListener, options, null, compilationUnits);
	}

	/**
	 * 获取{@link JavaSourceCompiler}
	 *
	 * @param parent 父{@link ClassLoader}
	 * @return {@link JavaSourceCompiler}
	 * @see JavaSourceCompiler#create(ClassLoader)
	 */
	public static JavaSourceCompiler getCompiler(ClassLoader parent) {
		return JavaSourceCompiler.create(parent);
	}
}
