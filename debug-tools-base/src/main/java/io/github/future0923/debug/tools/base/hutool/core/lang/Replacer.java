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
 * 替换器<br>
 * 通过实现此接口完成指定类型对象的替换操作，替换后的目标类型依旧为指定类型
 *
 * @author looly
 *
 * @param <T> 被替换操作的类型
 * @since 4.1.5
 */
@FunctionalInterface
public interface Replacer<T> {

	/**
	 * 替换指定类型为目标类型
	 *
	 * @param t 被替换的对象
	 * @return 替代后的对象
	 */
	T replace(T t);
}
