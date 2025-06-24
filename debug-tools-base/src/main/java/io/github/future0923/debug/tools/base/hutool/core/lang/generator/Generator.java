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
package io.github.future0923.debug.tools.base.hutool.core.lang.generator;

/**
 * 生成器泛型接口<br>
 * 通过实现此接口可以自定义生成对象的策略
 *
 * @param <T> 生成对象类型
 * @since 5.4.3
 */
public interface Generator<T> {

	/**
	 * 生成新的对象
	 *
	 * @return 新的对象
	 */
	T next();
}
