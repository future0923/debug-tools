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
 * 规范化对象生成工具
 *
 * @author looly
 * @since 5.4.3
 */
public class InternUtil {

	/**
	 * 创建WeakHshMap实现的字符串规范化器
	 *
	 * @param <T> 规范对象的类型
	 * @return {@link Interner}
	 */
	public static <T> Interner<T> createWeakInterner(){
		return new WeakInterner<>();
	}

	/**
	 * 创建JDK默认实现的字符串规范化器
	 *
	 * @return {@link Interner}
	 * @see String#intern()
	 */
	public static Interner<String> createJdkInterner(){
		return new JdkStringInterner();
	}

	/**
	 * 创建字符串规范化器
	 *
	 * @param isWeak 是否创建使用WeakHashMap实现的Interner
	 * @return {@link Interner}
	 */
	public static Interner<String> createStringInterner(boolean isWeak){
		return isWeak ? createWeakInterner() : createJdkInterner();
	}
}
