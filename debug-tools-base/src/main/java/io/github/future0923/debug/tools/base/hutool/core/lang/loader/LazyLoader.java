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

/**
 * 懒加载加载器<br>
 * 在load方法被调用前，对象未被加载，直到被调用后才开始加载<br>
 * 此加载器常用于对象比较庞大而不一定被使用的情况，用于减少启动时资源占用问题<br>
 * 此加载器使用双重检查（Double-Check）方式检查对象是否被加载，避免多线程下重复加载或加载丢失问题
 *
 * @author looly
 *
 * @param <T> 被加载对象类型
 */
public abstract class LazyLoader<T> implements Loader<T>, Serializable {
	private static final long serialVersionUID = 1L;

	/** 被加载对象 */
	private volatile T object;

	/**
	 * 获取一个对象，第一次调用此方法时初始化对象然后返回，之后调用此方法直接返回原对象
	 */
	@Override
	public T get() {
		T result = object;
		if (result == null) {
			synchronized (this) {
				result = object;
				if (result == null) {
					object = result = init();
				}
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
