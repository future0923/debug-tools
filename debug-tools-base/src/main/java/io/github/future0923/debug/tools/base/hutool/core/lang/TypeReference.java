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
