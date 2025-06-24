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
package io.github.future0923.debug.tools.base.hutool.core.text.finder;

/**
 * 字符串查找接口，通过调用{@link #start(int)}查找开始位置，再调用{@link #end(int)}找结束位置
 *
 * @author looly
 * @since 5.7.14
 */
public interface Finder {

	int INDEX_NOT_FOUND = -1;

	/**
	 * 返回开始位置，即起始字符位置（包含），未找到返回-1
	 *
	 * @param from 查找的开始位置（包含）
	 * @return 起始字符位置，未找到返回-1
	 */
	int start(int from);

	/**
	 * 返回结束位置，即最后一个字符后的位置（不包含）
	 *
	 * @param start 找到的起始位置
	 * @return 结束位置，未找到返回-1
	 */
	int end(int start);

	/**
	 * 复位查找器，用于重用对象
	 * @return this
	 */
	default Finder reset(){
		return this;
	}
}
