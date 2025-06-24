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
package io.github.future0923.debug.tools.base.hutool.core.builder;

import java.io.Serializable;

/**
 * 建造者模式接口定义
 *
 * @param <T> 建造对象类型
 * @author Looly
 * @since 4.2.2
 */
public interface Builder<T> extends Serializable{
	/**
	 * 构建
	 *
	 * @return 被构建的对象
	 */
	T build();
}