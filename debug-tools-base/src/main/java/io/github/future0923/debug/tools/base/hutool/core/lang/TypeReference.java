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
package io.github.future0923.debug.tools.base.hutool.core.lang;

import io.github.future0923.debug.tools.base.hutool.core.util.TypeUtil;

import java.lang.reflect.Type;

/**
 * Type类型参考<br>
 * 通过构建一个类型参考子类，可以获取其泛型参数中的Type类型。例如：
 *
 * <pre>
 * TypeReference&lt;List&lt;String&gt;&gt; list = new TypeReference&lt;List&lt;String&gt;&gt;() {};
 * Type t = tr.getType();
 * </pre>
 *
 * 此类无法应用于通配符泛型参数（wildcard parameters），比如：{@code Class<?>} 或者 {@code List? extends CharSequence>}
 *
 * <p>
 * 此类参考FastJSON的TypeReference实现
 *
 * @author looly
 *
 * @param <T> 需要自定义的参考类型
 * @since 4.2.2
 */
public abstract class TypeReference<T> implements Type {

	/** 泛型参数 */
	private final Type type;

	/**
	 * 构造
	 */
	public TypeReference() {
		this.type = TypeUtil.getTypeArgument(getClass());
	}

	/**
	 * 获取用户定义的泛型参数
	 *
	 * @return 泛型参数
	 */
	public Type getType() {
		return this.type;
	}

	@Override
	public String toString() {
		return this.type.toString();
	}
}
