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
package io.github.future0923.debug.tools.base.hutool.core.lang;

import io.github.future0923.debug.tools.base.hutool.core.convert.Convert;
import io.github.future0923.debug.tools.base.hutool.core.util.NumberUtil;

import java.lang.reflect.Type;

/**
 * 片段表示，用于表示文本、集合等数据结构的一个区间。
 * @param <T> 数字类型，用于表示位置index
 *
 * @author looly
 * @since 5.5.3
 */
public interface Segment<T extends Number> {

	/**
	 * 获取起始位置
	 *
	 * @return 起始位置
	 */
	T getStartIndex();

	/**
	 * 获取结束位置
	 *
	 * @return 结束位置
	 */
	T getEndIndex();

	/**
	 * 片段长度，默认计算方法为abs({@link #getEndIndex()} - {@link #getEndIndex()})
	 *
	 * @return 片段长度
	 */
	default T length(){
		final T start = Assert.notNull(getStartIndex(), "Start index must be not null!");
		final T end = Assert.notNull(getEndIndex(), "End index must be not null!");
		return Convert.convert((Type) start.getClass(), NumberUtil.sub(end, start).abs());
	}
}
