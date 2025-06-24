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


