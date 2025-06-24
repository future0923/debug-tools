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
 * 无参数的函数对象<br>
 * 接口灵感来自于<a href="http://actframework.org/">ActFramework</a><br>
 * 一个函数接口代表一个一个函数，用于包装一个函数为对象<br>
 * 在JDK8之前，Java的函数并不能作为参数传递，也不能作为返回值存在，此接口用于将一个函数包装成为一个对象，从而传递对象
 *
 * @author Looly
 *
 * @param <R> 返回值类型
 * @since 4.5.2
 */
@FunctionalInterface
public interface Func0<R> extends Serializable {
	/**
	 * 执行函数
	 *
	 * @return 函数执行结果
	 * @throws Exception 自定义异常
	 */
	R call() throws Exception;

	/**
	 * 执行函数，异常包装为RuntimeException
	 *
	 * @return 函数执行结果
	 * @since 5.3.6
	 */
	default R callWithRuntimeException(){
		try {
			return call();
		} catch (Exception e) {
			throw ExceptionUtil.wrapRuntime(e);
		}
	}
}
