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
package io.github.future0923.debug.tools.base.hutool.http;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Http拦截器接口，通过实现此接口，完成请求发起前或结束后对请求的编辑工作
 *
 * @param <T> 过滤参数类型，HttpRequest或者HttpResponse
 * @author looly
 * @since 5.7.16
 */
@FunctionalInterface
public interface HttpInterceptor<T extends HttpBase<T>> {

	/**
	 * 处理请求
	 *
	 * @param httpObj 请求或响应对象
	 */
	void process(T httpObj);

	/**
	 * 拦截器链
	 *
	 * @param <T> 过滤参数类型，HttpRequest或者HttpResponse
	 * @author looly
	 * @since 5.7.16
	 */
	class Chain<T extends HttpBase<T>> implements io.github.future0923.debug.tools.base.hutool.core.lang.Chain<HttpInterceptor<T>, Chain<T>> {
		private final List<HttpInterceptor<T>> interceptors = new LinkedList<>();

		@Override
		public Chain<T> addChain(HttpInterceptor<T> element) {
			interceptors.add(element);
			return this;
		}

		@Override
		public Iterator<HttpInterceptor<T>> iterator() {
			return interceptors.iterator();
		}

		/**
		 * 清空
		 *
		 * @return this
		 * @since 5.8.0
		 */
		public Chain<T> clear() {
			interceptors.clear();
			return this;
		}
	}
}
