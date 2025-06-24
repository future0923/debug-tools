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

import io.github.future0923.debug.tools.base.hutool.core.map.WeakConcurrentMap;

import java.lang.ref.WeakReference;

/**
 * 使用WeakHashMap(线程安全)存储对象的规范化对象，注意此对象需单例使用！<br>
 *
 * @author looly
 * @since 5.4.3
 */
public class WeakInterner<T> implements Interner<T> {

	private final WeakConcurrentMap<T, WeakReference<T>> cache = new WeakConcurrentMap<>();

	public T intern(T sample) {
		if (sample == null) {
			return null;
		}
		T val;
		do {
			val = this.cache.computeIfAbsent(sample, WeakReference::new).get();
		} while (val == null);
		return val;
	}
}
