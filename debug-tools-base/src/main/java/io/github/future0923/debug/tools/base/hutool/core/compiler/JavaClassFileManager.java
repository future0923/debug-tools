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

import io.github.future0923.debug.tools.base.hutool.core.io.resource.FileObjectResource;
import io.github.future0923.debug.tools.base.hutool.core.lang.ResourceClassLoader;
import io.github.future0923.debug.tools.base.hutool.core.util.ClassLoaderUtil;
import io.github.future0923.debug.tools.base.hutool.core.util.ObjectUtil;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import java.util.HashMap;
import java.util.Map;

/**
 * Java 字节码文件对象管理器
 *
 * <p>
 * 正常我们使用javac命令编译源码时会将class文件写入到磁盘中，但在运行时动态编译类不适合保存在磁盘中
 * 我们采取此对象来管理运行时动态编译类生成的字节码。
 * </p>
 *
 * @author lzpeng
 * @since 5.5.2
 */
class JavaClassFileManager extends ForwardingJavaFileManager<JavaFileManager> {

	/**
	 * 存储java字节码文件对象映射
	 */
	private final Map<String, FileObjectResource> classFileObjectMap = new HashMap<>();

	/**
	 * 加载动态编译生成类的父类加载器
	 */
	private final ClassLoader parent;

	/**
	 * 构造
	 *
	 * @param parent      父类加载器
	 * @param fileManager 字节码文件管理器
	 */
	protected JavaClassFileManager(ClassLoader parent, JavaFileManager fileManager) {
		super(fileManager);
		this.parent = ObjectUtil.defaultIfNull(parent, ClassLoaderUtil::getClassLoader);
	}

	/**
	 * 获得动态编译生成的类的类加载器
	 *
	 * @param location 源码位置
	 * @return 动态编译生成的类的类加载器
	 */
	@Override
	public ClassLoader getClassLoader(final Location location) {
		return new ResourceClassLoader<>(this.parent, this.classFileObjectMap);
	}

	/**
	 * 获得Java字节码文件对象
	 * 编译器编译源码时会将Java源码对象编译转为Java字节码对象
	 *
	 * @param location  源码位置
	 * @param className 类名
	 * @param kind      文件类型
	 * @param sibling   Java源码对象
	 * @return Java字节码文件对象
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(final Location location, final String className, final Kind kind, final FileObject sibling) {
		final JavaFileObject javaFileObject = new JavaClassFileObject(className);
		this.classFileObjectMap.put(className, new FileObjectResource(javaFileObject));
		return javaFileObject;
	}

}
