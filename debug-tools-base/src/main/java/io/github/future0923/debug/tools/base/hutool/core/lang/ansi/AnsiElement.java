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
package io.github.future0923.debug.tools.base.hutool.core.lang.ansi;

/**
 * ANSI可转义节点接口，实现为ANSI颜色等
 *
 * <p>来自Spring Boot</p>
 *
 * @author Phillip Webb
 */
public interface AnsiElement {

	/**
	 * @return ANSI转义编码
	 */
	@Override
	String toString();

	/**
	 * 获取ANSI代码，默认返回-1
	 * @return ANSI代码
	 * @since 5.8.7
	 */
	default int getCode(){
		return -1;
	}
}
