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
package io.github.future0923.debug.tools.base.hutool.core.lang.func;

import java.util.function.Supplier;

/**
 * 5参数Supplier
 *
 * @param <T>  目标   类型
 * @param <P1> 参数一 类型
 * @param <P2> 参数二 类型
 * @param <P3> 参数三 类型
 * @param <P4> 参数四 类型
 * @param <P5> 参数五 类型
 * @author TomXin
 * @since 5.7.21
 */
@FunctionalInterface
public interface Supplier5<T, P1, P2, P3, P4, P5> {

	/**
	 * 生成实例的方法
	 *
	 * @param p1 参数一
	 * @param p2 参数二
	 * @param p3 参数三
	 * @param p4 参数四
	 * @param p5 参数五
	 * @return 目标对象
	 */
	T get(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5);

	/**
	 * 将带有参数的Supplier转换为无参{@link Supplier}
	 *
	 * @param p1 参数1
	 * @param p2 参数2
	 * @param p3 参数3
	 * @param p4 参数4
	 * @param p5 参数5
	 * @return {@link Supplier}
	 */
	default Supplier<T> toSupplier(P1 p1, P2 p2, P3 p3, P4 p4, P5 p5) {
		return () -> get(p1, p2, p3, p4, p5);
	}
}
