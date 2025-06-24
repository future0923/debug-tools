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
package io.github.future0923.debug.tools.base.hutool.core.bean.copier;

import java.lang.reflect.Type;

/**
 * 值提供者，用于提供Bean注入时参数对应值得抽象接口<br>
 * 继承或匿名实例化此接口<br>
 * 在Bean注入过程中，Bean获得字段名，通过外部方式根据这个字段名查找相应的字段值，然后注入Bean<br>
 *
 * @author Looly
 * @param <T> KEY类型，一般情况下为 {@link String}
 *
 */
public interface ValueProvider<T>{

	/**
	 * 获取值<br>
	 * 返回值一般需要匹配被注入类型，如果不匹配会调用默认转换 Convert#convert(Type, Object)实现转换
	 *
	 * @param key Bean对象中参数名
	 * @param valueType 被注入的值的类型
	 * @return 对应参数名的值
	 */
	Object value(T key, Type valueType);

	/**
	 * 是否包含指定KEY，如果不包含则忽略注入<br>
	 * 此接口方法单独需要实现的意义在于：有些值提供者（比如Map）key是存在的，但是value为null，此时如果需要注入这个null，需要根据此方法判断
	 *
	 * @param key Bean对象中参数名
	 * @return 是否包含指定KEY
	 */
	boolean containsKey(T key);
}
