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

/**
 * 编辑器接口，常用于对于集合中的元素做统一编辑<br>
 * 此编辑器两个作用：
 *
 * <pre>
 * 1、如果返回值为{@code null}，表示此值被抛弃
 * 2、对对象做修改
 * </pre>
 *
 * @param <T> 被编辑对象类型
 * @author Looly
 */
@FunctionalInterface
public interface Editor<T> {
	/**
	 * 修改过滤后的结果
	 *
	 * @param t 被过滤的对象
	 * @return 修改后的对象，如果被过滤返回{@code null}
	 */
	T edit(T t);
}
