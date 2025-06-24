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

/**
 * 对象加载抽象接口<br>
 * 通过实现此接口自定义实现对象的加载方式，例如懒加载机制、多线程加载等
 *
 * @author looly
 *
 * @param <T> 对象类型
 */
@FunctionalInterface
public interface Loader<T> {

	/**
	 * 获取一个准备好的对象<br>
	 * 通过准备逻辑准备好被加载的对象，然后返回。在准备完毕之前此方法应该被阻塞
	 *
	 * @return 加载完毕的对象
	 */
	T get();
}
