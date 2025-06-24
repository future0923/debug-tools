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
package io.github.future0923.debug.tools.base.hutool.core.util;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * 引用工具类，主要针对{@link Reference} 工具化封装<br>
 * 主要封装包括：
 * <pre>
 * 1. {@link SoftReference} 软引用，在GC报告内存不足时会被GC回收
 * 2. {@link WeakReference} 弱引用，在GC时发现弱引用会回收其对象
 * 3. {@link PhantomReference} 虚引用，在GC时发现虚引用对象，会将{@link PhantomReference}插入{@link ReferenceQueue}。 此时对象未被真正回收，要等到{@link ReferenceQueue}被真正处理后才会被回收。
 * </pre>
 *
 * @author looly
 * @since 3.1.2
 */
public class ReferenceUtil {

	/**
	 * 获得引用
	 *
	 * @param <T> 被引用对象类型
	 * @param type 引用类型枚举
	 * @param referent 被引用对象
	 * @return {@link Reference}
	 */
	public static <T> Reference<T> create(ReferenceType type, T referent) {
		return create(type, referent, null);
	}

	/**
	 * 获得引用
	 *
	 * @param <T> 被引用对象类型
	 * @param type 引用类型枚举
	 * @param referent 被引用对象
	 * @param queue 引用队列
	 * @return {@link Reference}
	 */
	public static <T> Reference<T> create(ReferenceType type, T referent, ReferenceQueue<T> queue) {
		switch (type) {
		case SOFT:
			return new SoftReference<>(referent, queue);
		case WEAK:
			return new WeakReference<>(referent, queue);
		case PHANTOM:
			return new PhantomReference<>(referent, queue);
		default:
			return null;
		}
	}

	/**
	 * 引用类型
	 *
	 * @author looly
	 *
	 */
	public enum ReferenceType {
		/** 软引用，在GC报告内存不足时会被GC回收 */
		SOFT,
		/** 弱引用，在GC时发现弱引用会回收其对象 */
		WEAK,
		/**
		 * 虚引用，在GC时发现虚引用对象，会将{@link PhantomReference}插入{@link ReferenceQueue}。 <br>
		 * 此时对象未被真正回收，要等到{@link ReferenceQueue}被真正处理后才会被回收。
		 */
		PHANTOM
	}

}
