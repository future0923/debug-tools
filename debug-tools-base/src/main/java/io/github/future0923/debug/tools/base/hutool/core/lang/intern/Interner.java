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
package io.github.future0923.debug.tools.base.hutool.core.lang.intern;

/**
 * 规范化表示形式封装<br>
 * 所谓规范化，即当两个对象equals时，规范化的对象则可以实现==<br>
 * 此包中的相关封装类似于 {@link String#intern()}
 *
 * @param <T> 规范化的对象类型
 * @author looly
 * @since 5.4.3
 */
public interface Interner<T> {

	/**
	 * 返回指定对象对应的规范化对象，sample对象可能有多个，但是这些对象如果都equals，则返回的是同一个对象
	 *
	 * @param sample 对象
	 * @return 样例对象
	 */
	T intern(T sample);
}