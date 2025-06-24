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
package io.github.future0923.debug.tools.base.hutool.core.map;

import io.github.future0923.debug.tools.base.hutool.core.util.ReferenceUtil;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentMap;

/**
 * 线程安全的WeakMap实现<br>
 * 参考：jdk.management.resource.internal.WeakKeyConcurrentHashMap
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author looly
 * @since 5.8.0
 */
public class WeakConcurrentMap<K, V> extends ReferenceConcurrentMap<K, V> {

	/**
	 * 构造
	 */
	public WeakConcurrentMap() {
		this(new SafeConcurrentHashMap<>());
	}

	/**
	 * 构造
	 *
	 * @param raw {@link ConcurrentMap}实现
	 */
	public WeakConcurrentMap(ConcurrentMap<Reference<K>, V> raw) {
		super(raw, ReferenceUtil.ReferenceType.WEAK);
	}
}
