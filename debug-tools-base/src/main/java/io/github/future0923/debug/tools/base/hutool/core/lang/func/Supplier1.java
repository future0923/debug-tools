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

import java.util.function.Supplier;

/**
 * 1参数Supplier
 *
 * @param <T>  目标   类型
 * @param <P1> 参数一 类型
 * @author TomXin
 * @since 5.7.21
 */
@FunctionalInterface
public interface Supplier1<T, P1> {
	/**
	 * 生成实例的方法
	 *
	 * @param p1 参数一
	 * @return 目标对象
	 */
	T get(P1 p1);

	/**
	 * 将带有参数的Supplier转换为无参{@link Supplier}
	 *
	 * @param p1 参数1
	 * @return {@link Supplier}
	 */
	default Supplier<T> toSupplier(P1 p1) {
		return () -> get(p1);
	}
}
