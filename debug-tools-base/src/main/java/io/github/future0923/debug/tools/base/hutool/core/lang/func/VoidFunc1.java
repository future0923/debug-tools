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
package io.github.future0923.debug.tools.base.hutool.core.lang.func;

import io.github.future0923.debug.tools.base.hutool.core.exceptions.ExceptionUtil;

import java.io.Serializable;

/**
 * 函数对象<br>
 * 接口灵感来自于<a href="http://actframework.org/">ActFramework</a><br>
 * 一个函数接口代表一个一个函数，用于包装一个函数为对象<br>
 * 在JDK8之前，Java的函数并不能作为参数传递，也不能作为返回值存在，此接口用于将一个函数包装成为一个对象，从而传递对象
 *
 * @author Looly
 *
 * @since 3.2.3
 */
@FunctionalInterface
public interface VoidFunc1<P> extends Serializable {

	/**
	 * 执行函数
	 *
	 * @param parameter 参数
	 * @throws Exception 自定义异常
	 */
	void call(P parameter) throws Exception;

	/**
	 * 执行函数，异常包装为RuntimeException
	 *
	 * @param parameter 参数
	 * @since 5.3.6
	 */
	default void callWithRuntimeException(P parameter){
		try {
			call(parameter);
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}
}
