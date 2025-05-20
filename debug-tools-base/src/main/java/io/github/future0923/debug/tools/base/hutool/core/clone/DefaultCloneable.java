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
package io.github.future0923.debug.tools.base.hutool.core.clone;


import io.github.future0923.debug.tools.base.hutool.core.util.ReflectUtil;

/**
 * 克隆默认实现接口，用于实现返回指定泛型类型的克隆方法
 *
 * @param <T> 泛型类型
 * @since 5.7.17
 */
public interface DefaultCloneable<T> extends java.lang.Cloneable {

	/**
	 * 浅拷贝，提供默认的泛型返回值的clone方法。
	 *
	 * @return obj
	 */
	default T clone0() {
		try {
			return ReflectUtil.invoke(this, "clone");
		} catch (Exception e) {
			throw new CloneRuntimeException(e);
		}
	}
}


