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
package io.github.future0923.debug.tools.base.hutool.core.lang.loader;


import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 原子引用加载器<br>
 * 使用{@link AtomicReference} 实懒加载，过程如下
 * <pre>
 * 1. 检查引用中是否有加载好的对象，有则返回
 * 2. 如果没有则初始化一个对象，并再次比较引用中是否有其它线程加载好的对象，无则加入，有则返回已有的
 * </pre>
 *
 * 当对象未被创建，对象的初始化操作在多线程情况下可能会被调用多次（多次创建对象），但是总是返回同一对象
 *
 * @author looly
 *
 * @param <T> 被加载对象类型
 */
public abstract class AtomicLoader<T> implements Loader<T>, Serializable {
	private static final long serialVersionUID = 1L;

	/** 被加载对象的引用 */
	private final AtomicReference<T> reference = new AtomicReference<>();

	/**
	 * 获取一个对象，第一次调用此方法时初始化对象然后返回，之后调用此方法直接返回原对象
	 */
	@Override
	public T get() {
		T result = reference.get();

		if (result == null) {
			result = init();
			if (false == reference.compareAndSet(null, result)) {
				// 其它线程已经创建好此对象
				result = reference.get();
			}
		}

		return result;
	}

	/**
	 * 初始化被加载的对象<br>
	 * 如果对象从未被加载过，调用此方法初始化加载对象，此方法只被调用一次
	 *
	 * @return 被加载的对象
	 */
	protected abstract T init();
}
