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
