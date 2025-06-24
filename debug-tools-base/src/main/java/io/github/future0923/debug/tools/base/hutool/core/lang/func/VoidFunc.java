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
 * @param <P> 参数类型
 * @since 3.1.0
 */
@FunctionalInterface
public interface VoidFunc<P> extends Serializable {

	/**
	 * 执行函数
	 *
	 * @param parameters 参数列表
	 * @throws Exception 自定义异常
	 */
	@SuppressWarnings("unchecked")
	void call(P... parameters) throws Exception;

	/**
	 * 执行函数，异常包装为RuntimeException
	 *
	 * @param parameters 参数列表
	 */
	@SuppressWarnings("unchecked")
	default void callWithRuntimeException(P... parameters){
		try {
			call(parameters);
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}
}
